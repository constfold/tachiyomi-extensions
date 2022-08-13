include(":core")

include(":lib-dataimage")
project(":lib-dataimage").projectDir = File("lib/dataimage")

if (System.getenv("CI") == null || System.getenv("CI_MODULE_GEN") == "true") {
    // Local development (full project build)

    include(":multisrc")
    project(":multisrc").projectDir = File("multisrc")

    // Loads all extensions
    File(rootDir, "src").eachDir { dir ->
        dir.eachDir { subdir ->
            val name = ":extensions:individual:${dir.name}:${subdir.name}"
            include(name)
            project(name).projectDir = File("src/${dir.name}/${subdir.name}")
        }
    }
    // Loads all generated extensions from multisrc
    File(rootDir, "generated-src").eachDir { dir ->
        dir.eachDir { subdir ->
            val name = ":extensions:multisrc:${dir.name}:${subdir.name}"
            include(name)
            project(name).projectDir = File("generated-src/${dir.name}/${subdir.name}")
        }
    }

    /**
     * If you're developing locally and only want to work with a single module,
     * comment out the parts above and uncomment below.
     */
//    val lang = "all"
//    val name = "mangadex"
//    val projectName = ":extensions:individual:$lang:$name"
//    val projectName = ":extensions:multisrc:$lang:$name"
//    include(projectName)
//    project(projectName).projectDir = File("src/${lang}/${name}")
//    project(projectName).projectDir = File("generated-src/${lang}/${name}")
} else {
    // Running in CI (GitHub Actions)

    val lang = "zh"
    val name = "copymangas"
    val projectName = ":extensions:individual:$lang:$name"
    include(projectName)
    project(projectName).projectDir = File("src/${lang}/${name}")
}

fun File.getChunk(chunk: Int, chunkSize: Int): List<File>? {
    return listFiles()
        // Lang folder
        ?.filter { it.isDirectory }
        // Extension subfolders
        ?.mapNotNull { dir -> dir.listFiles()?.filter { it.isDirectory } }
        ?.flatten()
        ?.sortedBy { it.name }
        ?.chunked(chunkSize)
        ?.get(chunk)
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}
