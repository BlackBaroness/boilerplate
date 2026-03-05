plugins {
    `kotlin-conventions`
    `publish-conventions`
}

dependencies {
    api(project(":boilerplate-main"))
    api(project(":boilerplate-adventure"))
    api(project(":boilerplate-paper"))
}
