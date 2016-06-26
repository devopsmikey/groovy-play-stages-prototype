def env = System.getenv()

STAGES = [ "clean", "deploy", "config", "execute" ]

// following IS a map
STAGE_STEPS = [ 
    clean: ["1", "2", "3"], 
    deploy: ["deploy-build", "deploy-test-scripts"], 
    config: ["1", "2", "3"], 
    execute: ["1", "2", "3"], 
]

def validate_stage_step(stage_name, step_name) {
    // VALIDATE STAGE STEP
    if (!STAGE_STEPS[stage_name].contains(step_name)) {
        throw new Exception("STAGE_STEP_NOT_FOUND - whoa buddy, slow down! Step '$step_name' not found in stage '$stage_name' whose valid steps are: ${STAGE_STEPS[stage_name]}\n")
    }
}

// better to do this with a map, find entry, then just walk thru...
// ALSO - do we validate stages and steps BEFORE DOING ANY?  probably a good thing...
//  does this suggest a validate parameter?
//  can we have a stage take an arg list overall?

// DO WE ENFORCE VALIDATE IN THE DESIGN?  just inject it and be done with it 
// as a design requirement?

// todo suppose we could also have a STOP_STAGE and a STOP_STAGE_STEP, eh?

def deploy(STEP, String... ARGS) {
    println "STAGES: $STAGES"
    println "STAGE_STEPS: " + STAGE_STEPS["deploy"]
    def STAGE="deploy"
    println "\nHere in stage $STAGE with ARGS: $ARGS"
    if (!STEP) {
        STEP=STAGE_STEPS["deploy"][0]
    } else {
        // validate step name
        validate_stage_step("deploy",STEP)
    }
    switch(STEP) {
        case "1":
            println "step1"
        case "2":
            println "step2"
            break
        default:
            println "ERROR - undefined step: $STEP; skipping stage $STAGE"
    }
}


def stage_deploy(String... args) {
    myname = args[0].toString()
    mystep = args[1].toString()
    mystepargs = args.drop(2)

    //validate_stage_step(myname, mystep)
    println myname + mystep
    validate_stage_step(myname, mystep)

    println "Enter stage: '$myname' for stage step '$mystep' with args: '$mystepargs'"
    return
}

// main area

// TODO check if stage undefined, if not set to first

start_stage = env.STAGE
step = env.STAGE_STEP
stepargs = env.STAGE_STEP_ARGS

if (!start_stage) {
    start_stage = STAGES[0]
    println "No or empty start_stage specified, setting to first stage: $stage"
}

if (!step) {
    step = STAGE_STEPS[start_stage][0]
}

// VALIDATE STAGE
if (!STAGES.contains(start_stage)) {
    println "ERROR - Stage $start_stage' not found in STAGES: $STAGES\n"
    throw new Exception("STAGE_NOT_FOUND - whoa buddy, slow down! Stage not found: "+start_stage)
}

validate_stage_step(start_stage,step)

println "start_stage set to: $start_stage"

// call stages in order, starting at start stage

started=false

STAGES.each { loopstage ->
    arglist = loopstage // default arglist is stage name only
   
    // are we starting at a different entrypoint? 
    if (loopstage.equals(start_stage)) {
        started=true
        arglist = [ loopstage, step, stepargs ].toArray(new String[3]) // args for only start-stage 
    }

    if (!started) {
        println "Skipping stage: $loopstage\n"
    }

    // test for continuing steps following where we started
    if (started) { 
        stage_method_name = "stage_$loopstage"
        println "\nCalling stage: $loopstage with arglist: $arglist"
        try {
        "${stage_method_name}"(arglist)
        } catch (groovy.lang.MissingMethodException e) { 
            println ("\nDoh!")
            println("STAGE_NOT_IMPLEMENTED - whoa buddy, slow down! Stage '$loopstage' not yet implemented! Sorry.")
            //throw new Exception("STAGE_NOT_IMPLEMENTED - whoa buddy, slow down! Stage '$loopstage' not yet implemented! Sorry.")
        }
        println "Returned from stage: $loopstage\n"
    }
}


println 'all done'

