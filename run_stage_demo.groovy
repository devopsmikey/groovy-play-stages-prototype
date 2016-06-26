def env = System.getenv()

// optional env vars to pass into this script
// STAGE = starting stage name
// STEP = starting step
// STAGE_STEP_ARGS = args for use on first step

start_stage = env.STAGE
step = env.STAGE_STEP
stepargs = env.STAGE_STEP_ARGS

def STAGES = [ "build", "sanity", "unit_test", "deploy", "sys_test", "promote" ]
//STAGES = [ "deploy"]

//!! Build the stage steps MAP

STAGE_STEPS = [:]
STAGE_INSTANCES = [:]

STAGES.each { stage_name ->
    stage_classname = "${stage_name}_stage"
    try {
        instance = this.class.classLoader.loadClass( stage_classname, true, false )?.newInstance()
        STAGE_INSTANCES[stage_name] = instance
        println "Testing presumed stage '$stage_name' at instance: ${instance.get_stage_name()}"
        STAGE_STEPS[stage_name] = STAGE_INSTANCES[stage_name].get_step_names()
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

def do_stage(String mystage, String mystep, String... mystepargs) {
    if (!mystep) { mystep = STAGE_INSTANCES[mystage].get_step_names()[0] }
    if (!mystepargs) { mystepargs = null }

    println "DEBUG: do_stage($mystage, $mystep, $mystepargs) was called"

    validate_stage_step(mystage, mystep)

    println "Enter stage: '$mystage' for stage step '$mystep' with args: '$mystepargs'"

    STAGE_STEPS[mystage].each { loopstep ->
        // are we starting at a different entrypoint? 
        if (loopstep.equals(start_stage)) {
            started=true
        }

	    if (!started) {
	        println "    Skipping step: $loopstep\n"
	    }
	
	    // test for continuing steps following where we started
	    if (started) { 
            stage_instance = STAGE_INSTANCES[mystage]
            stage_step_method_name = "Step_${loopstep}"
            println "\n    Calling stage/step: ${stage_step_method_name}() with $mystage, $mystep, $mystepargs"

//                if (stage_instance.respondsTo(stage_step_method_name)) {
//                    println "    AAAAAAAAAAAAAGUH DEBUG: ${stage_instance.get_stage_name()} responds to $stage_step_method_name TRUE"
//                } else {
//                    println "AAAAAAAAAAAAAGUH DEBUG: FALSE ${stage_instance.get_stage_name()} responds to $stage_step_method_name FALSE"
//                }

            try {
                stage_instance."${stage_step_method_name}"(mystage, mystep, mystepargs)
                mystepargs=null // reset after first call
            } catch (groovy.lang.MissingMethodException e) { 
                println ("\nDoh!"+e.getMessage())
                println("STAGE_STEP_NOT_IMPLEMENTED - whoa buddy, slow down! Stage/step method '${stage_instance.stage_class_name()}.${stage_step_method_name}()' not yet implemented! Sorry. (i.e. groovy.lang.MissingMethodException)")
                //throw new Exception("STAGE_NOT_IMPLEMENTED - whoa buddy, slow down! Stage '$loopstage' not yet implemented! Sorry.")
            }
            println "    Returned from step: $loopstep\n"
        }  // fi started
    } // STAGE_STEPS loop
} // end do_stage

// main area

// TODO check if stage undefined, if not set to first


if (!start_stage) {
    start_stage = STAGES[0]
    println "No or empty start_stage specified, setting to first stage: $start_stage"
}

// VALIDATE user-supplied STAGE argument
if (!STAGES.contains(start_stage)) {
    println "ERROR - Stage '$start_stage' not found in STAGES: $STAGES\n"
    throw new Exception("STAGE_NOT_FOUND - whoa buddy, slow down! Stage not found: "+start_stage)
}

println "start_stage is $start_stage, ${STAGE_INSTANCES[start_stage]}"
if (!step) {
    step = (STAGE_INSTANCES[start_stage]).get_step_names()[0]
}

validate_stage_step(start_stage,step)

println "start_stage set to: $start_stage"

// call stages in order, starting at start stage

started=false

STAGES.each { loopstage ->
    println "STAGE loop arg list set to: $loopstage, $step, $stepargs"
   
    // are we starting at a different entrypoint? 
    if (loopstage.equals(start_stage)) {
        started=true
    }

    if (!started) {
        println "Skipping stage: $loopstage\n"
    }

    // test for continuing steps following where we started
    if (started) { 
        println "\nCalling do_stage for stage: $loopstage with arg list: $loopstage, $step, $stepargs"
        do_stage(loopstage, step, stepargs)
        println "Returned from stage: $loopstage\n"
        // reset step and stepargs
        step = null
        stepargs = null
        }
    }

println 'all done'

