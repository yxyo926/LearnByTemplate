/**
 * 处理国家数据合并逻辑
 *
 * @param existList  已存在的对象数组 (List of Maps/Objects)
 * @param idStr      以逗号分割的字符串 (e.g., "1,2,3")
 * @param country    用于组装新数据的模板对象 (Map/Object)
 * @return           需要插入的新数据集合
 */
def buildInsertList(List existList, String idStr, Map country) {
    // 1. 非空校验：如果传入的字符串为空，则无需处理，直接返回空集合
    if (!idStr?.trim()) {
        return []
    }

    // 2. 解析字符串并过滤掉空白项
    // split(',') 后使用 collect{ it.trim() } 去除空格，findAll{ it } 过滤空字符串
    List targetIds = idStr.split(',').collect { it.trim() }.findAll { it }

    // 如果解析后没有有效ID，直接返回
    if (!targetIds) {
        return []
    }

    // 3. 如果对象数组为空，直接将字符串拆开组装成插入集合
    if (!existList) {
        return targetIds.collect { createNewCountry(it, country) }
    }

    // 4. 提取已存在数组中的 countryId，放入 Set 以提升比对效率 (O(1) 查找)
    // Groovy 的 *. 操作符 (Spread Operator) 可以极其方便地提取集合中对象的某个属性
    Set existIdSet = existList*.countryId.findAll { it != null }.toSet()

    // 5. 筛选出不在已存在集合中的 ID，并组装成新对象
    return targetIds
            .findAll { !existIdSet.contains(it) }
            .collect { createNewCountry(it, country) }
}

/**
 * 辅助方法：基于模板对象和新的ID创建新对象
 * 这里使用 Groovy 的 Map 构造器语法，非常方便
 */
private Map createNewCountry(String countryId, Map template) {
    // 复制模板对象的所有属性，并覆盖 countryId
    // '+' 操作符在 Groovy Map 中用于合并，且右侧优先级更高
    return template + [countryId: countryId]
}

// --- 测试入口 ---
def main() {
    // 模拟已存在的数据 (Groovy 中直接使用 Map 模拟对象，非常轻量)
    def existList = [
            [countryId: "1", countryName: "中国"],
            [countryId: "2", countryName: "美国"]
    ]

    // 模拟传入的字符串 (包含已存在的1,2，以及不存在的3,4)
    String idStr = "1, 2, 3, 4, , 5"

    // 模拟模板对象
    def template = [countryName: "默认国家", status: "ACTIVE"]

    def insertList = buildInsertList(existList, idStr, template)

    println "需要插入的数据: ${insertList}"
}

main()

/**
 * 通用集合差集与组装处理器
 * @param <T> 已存在的数据对象类型
 * @param <R> 最终组装出的目标对象类型
 */
class CollectionDiffProcessor<T, R> {

    /**
     * 核心处理模板方法
     *
     * @param existList      已存在的对象集合
     * @param rawStr         原始字符串（如逗号分隔的ID串）
     * @param config         处理配置闭包
     * @return               需要插入的新数据集合
     */
    static <T, R> List<R> process(List<T> existList, String rawStr, Closure config) {
        // 1. 解析配置闭包
        def cfg = [:]
        config.delegate = cfg
        config.resolveStrategy = Closure.DELEGATE_FIRST
        config()

        // 2. 非空校验与字符串解析
        if (!rawStr?.trim()) return []
        List targetKeys = rawStr.split(cfg.delimiter ?: ',')
                .collect { it.trim() }
                .findAll { it }
        if (!targetKeys) return []

        // 3. 如果原集合为空，直接全量组装
        if (!existList) {
            return targetKeys.collect { cfg.assembler(it, null) }
        }

        // 4. 提取原集合的标识符并转为 Set (O(1) 查找)
        Set existKeySet = existList.collect { cfg.keyExtractor(it) }
                .findAll { it != null }
                .toSet()

        // 5. 过滤差集并组装新对象
        return targetKeys
                .findAll { !existKeySet.contains(it) }
                .collect { key ->
                    // 尝试从原集合中找到对应的原对象（可选，方便全量复制）
                    T matchedExist = existList.find { cfg.keyExtractor(it) == key }
                    return cfg.assembler(key, matchedExist)
                }
    }
}