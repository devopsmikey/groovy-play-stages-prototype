// unit_test_stage.groovy -- a template for a stage file

class unit_test_stage extends base_stage_class {

// name of this stage - must be valid in a Java class name

def get_stage_name() {
    return "unit_test"
}

// list of the step names for this stage

def get_step_names() {
    return [ "STEP1_NAME", "STEP2_NAME" ]
}

// create as many of these as are in step_names[] above
// being sure to prefix them with "Step_" string prefix

def Step_STEP1_NAME (stage, step, stepargs) {
    print_step_header(stage, step, stepargs)

    // impl of step here ...
}

def Step_STEP2_NAME (stage, step, stepargs) {
    print_step_header(stage, step, stepargs)

    // impl of step here ...
}

}
