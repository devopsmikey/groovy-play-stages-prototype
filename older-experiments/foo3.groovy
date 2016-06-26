name="Stage_deploy"
step="Step_deploy_test_scripts"
instance = this.class.classLoader.loadClass( name, true, false )?.newInstance()
instance."$step"("testing")
println instance.stage_name
println instance.step_names

//name="Stage_josh"
//step="Step_deploy_test_scripts"
//instance = this.class.classLoader.loadClass( name, true, false )?.newInstance()
//instance."$step"("testing")


def env = System.getenv()

def STAGES = [ "build", "sanity", "unit_test", "deploy", "sys_test", "promote" ]
STAGES = [ "deploy"]

//!! Build the stage steps MAP

STAGE_STEPS = [:]
STAGE_INSTANCES = [:]

STAGES.each { stage_name ->
    stage_classname = "Stage_$stage_name"
    try {
        instance = this.class.classLoader.loadClass( stage_classname, true, false )?.newInstance()
        STAGE_INSTANCES[stage_name] = instance
        STAGE_STEPS[stage_name] = STAGE_INSTANCES[stage_name].step_names
    } catch (java.lang.ClassNotFoundException e) {
        throw new Exception("STAGE_CLASS_NOT_FOUND - whoa buddy, slow down! Stage class '$stage_classname' not found")
    }
}

println "\nFor STAGES: $STAGES"
println "I have constructed STAGE_STEPS as: "
STAGES.each { name ->
    println "$name: ${STAGE_STEPS[name]}"
}
println "\n"

//// following IS a map
//STAGE_STEPS = [ 
//    clean: ["1", "2", "3"], 
//    deploy: ["deploy-build", "deploy-test-scripts"], 
//    config: ["1", "2", "3"], 
//    execute: ["1", "2", "3"], 
//]

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


def do_stage(String... args) {
    mystage = args[0].toString()
    mystep = STAGE_STEPS[mystage][0] // default to first step of stage
    if (args.size() > 1) { mystep = args[1].toString() }
    if (args.size() > 2) { mystepargs = args.drop(2) }

    println "DEBUG: do_stage($mystage, $mystep, $mystepargs) was called"

    validate_stage_step(mystage, mystep)

    println "Enter stage: '$mystage' for stage step '$mystep' with args: '$mystepargs'"

    STAGE_STEPS[mystage].each { loopstep ->
        arglist = [ mystage, loopstep].toArray(new String[2])  // default arglist is stage name and step name only
        println "DEBUG: arglist is $arglist" 
        // are we starting at a different entrypoint? 
        if (loopstep.equals(start_stage)) {
            started=true
            arglist = [ mystage, loopstep, stepargs ].toArray(new String[3]) // args for only start-stage 
        }

	    if (!started) {
	        println "Skipping step: $loopstep\n"
	    }
	
	    // test for continuing steps following where we started
	    if (started) { 
            stage_class = STAGE_INSTANCES[mystage]
            stage_step_method_name = "Step_${loopstep}"
            println "\nCalling stage/step: ${stage_step_method_name}() with arglist: $arglist"

//                if (stage_class.respondsTo(stage_step_method_name)) {
//                    println "AAAAAAAAAAAAAGUH DEBUG: $stage_class.stage_name responds to $stage_step_method_name TRUE"
//                } else {
//                    println "AAAAAAAAAAAAAGUH DEBUG: FALSE $stage_class.stage_name responds to $stage_step_method_name FALSE"
//                }

            try {
                //"${stage_step_method_name}"(arglist) // old way
                stage_class."${stage_step_method_name}"(arglist)
            } catch (groovy.lang.MissingMethodException e) { 
                println ("\nDoh!")
                println("STAGE_STEP_NOT_IMPLEMENTED - whoa buddy, slow down! Stage/step '$stage_step_method_name' not yet implemented! Sorry. (i.e. groovy.lang.MissingMethodException)")
                //throw new Exception("STAGE_NOT_IMPLEMENTED - whoa buddy, slow down! Stage '$loopstage' not yet implemented! Sorry.")
            }
            println "Returned from step: $loopstep\n"
        }  // fi started
    } // STAGE_STEPS loop
} // end do_stage

// main area

// TODO check if stage undefined, if not set to first

start_stage = env.STAGE
step = env.STAGE_STEP
stepargs = env.STAGE_STEP_ARGS

if (!start_stage) {
    start_stage = STAGES[0]
    println "No or empty start_stage specified, setting to first stage: $stage"
}

// VALIDATE user-supplied STAGE argument
if (!STAGES.contains(start_stage)) {
    println "ERROR - Stage '$start_stage' not found in STAGES: $STAGES\n"
    throw new Exception("STAGE_NOT_FOUND - whoa buddy, slow down! Stage not found: "+start_stage)
}

println "\$\$\$\$\$\$\$\$\$\$\$\$ what the hell?  #############"

println "start_stage is $start_stage, ${STAGE_INSTANCES[start_stage]}"
if (!step) {
    step = (STAGE_INSTANCES[start_stage]).step_names[0]
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
        println "\nCalling do_stage for stage: $loopstage with arglist: $arglist"
        do_stage(arglist)
        println "Returned from stage: $loopstage\n"
        }
    }

println 'all done'

