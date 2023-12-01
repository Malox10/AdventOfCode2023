fun readResourceLines(path: String): List<String> = readResource(path).lines()
fun readResource(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("Resource: $path not found")