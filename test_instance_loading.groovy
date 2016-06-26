// this should work, if Stage_deploy.groovy is in same dir

name="deploy_stage"
step="Step_deploy_test_scripts"
instance = this.class.classLoader.loadClass( name, true, false )?.newInstance()

def do_stage(String... args) {
    instance."$step"(args)
}

println instance.get_stage_name()
println instance.get_step_names()
do_stage('deploy', 'Step_deploy_test_scripts', null)


// this should fail

name="Stage_josh"
step="Step_deploy_test_scripts"
instance = this.class.classLoader.loadClass( name, true, false )?.newInstance()
