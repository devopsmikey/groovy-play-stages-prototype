def env = System.getenv()


// better to do this with a map, find entry, then just walk thru...
// ALSO - do we validate stages and steps BEFORE DOING ANY?  probably a good thing...
//  does this suggest a validate parameter?
//  can we have a stage take an arg list overall?

def init(STEP) {
    def STAGE="init"
    println "here in second stage 'init'"
    if (!STEP) {
        STEP="1"
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

def clean(step) {
    println "here in first stage 'clean'"
}


// main area

// TODO check if stage undefined, if not set to first

stage = env.STAGE
step = env.STAGE_STEP

if (!stage) {
    stage = 'first'
    println "No or empty stage specified, setting to default $stage"
}
    println "stage set to: $stage"

    switch (stage) {
case "first":
    println 'clean'
    clean(step)
case "second":
    println 'second'
    init(step)
}
println 'all done'
