class base_stage_class {

def get_stage_name() {
  return "CHANGEME_base_stage_name"
}

def get_step_names() {
    return [ "CHANGEME_base_step_names" ]
}

def stage_class_name() {
    return get_stage_name() + "_stage"
}

def print_step_header(stagename, stepname, stepargs) 
{
    println "\n        #### I am STAGE '$stagename' STEP '$stepname' with arg list: $stepargs\n"
}

}
