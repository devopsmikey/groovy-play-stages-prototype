// Stage_deploy

class deploy_stage extends base_stage_class {

def get_stage_name() {
   return "deploy"
} 

def get_step_names() {
   return ["deploy_build", "deploy_test_scripts"]
}

def Step_deploy_build(stage, step, String... stepargs) {
    print_step_header(stage, step, stepargs)
}

def Step_deploy_test_scripts(stage, step, String... stepargs) {
    print_step_header(stage, step, stepargs)
}

}
