package core

class ComparisonGroup(comparisons: List<Comparison>) {
    private val _similarityMap = HashMap<Int, Comparison>(comparisons.size)
    private val _paths = HashSet<String>(comparisons.size)
    private var _deleted = false

    init {
        comparisons.associateByTo(_similarityMap) {
            Comparison.calcHash(it.path1, it.path2)
        }

        comparisons.forEach {
            _paths.add(it.path1)
            _paths.add(it.path2)
        }
    }

    val paths get() = requireAlive(_paths)
    var relative = getRelativeToFirst()
        private set

    fun removePath(path: String) {
        requirePath(path)
        
        _paths.remove(path)
        relative = getRelativeToFirst()

        val buffer = ArrayList<Int>()

        _similarityMap.forEach { (key, value) ->
            if (value.path1 == path || value.path2 == path) {
                buffer.add(key)
            }
        }

        buffer.forEach {
            _similarityMap.remove(it)
        }
    }

    fun markGroupDeleted() {
        _paths.clear()
        _deleted = true
    }

    fun changeRelativityAnchor(path: String) {
        requirePath(path)

        relative = getRelativeTo(path)
    }

    private fun getRelativeTo(anchor: String): RelativeComparisonGroup {
        val filtered = paths
            .asSequence()
            .filter { it != anchor }
            .mapNotNull { other ->
                val hash = Comparison.calcHash(anchor, other)
                _similarityMap[hash]
            }
            .map {
                RelativeComparison(
                    path = if (it.path1 == anchor) it.path2 else it.path1,
                    similarity = it.similarity
                )
            }
            .toList()

        return RelativeComparisonGroup(anchor, filtered)
    }

    private fun getRelativeToFirst(): RelativeComparisonGroup {
        return getRelativeTo(_paths.first())
    }

    private fun requirePath(path: String) {
        require(path in paths) {
            "Path $path is not in the group"
        }
    }

    private fun <T> requireAlive(instance: T): T {
        require(!_deleted) {
            "The group is deleted"
        }

        return instance
    }
}
