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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryDataProcessor {

    /**
     * 处理国家数据：比对已有数据，组装需要插入的新数据
     *
     * @param existArray   已存在的对象数组 (JSONArray)
     * @param idsStr       以逗号分割的ID字符串
     * @param country      国家模板对象 (JSONObject)，用于组装新数据
     * @return 需要插入的数据集合 (JSONArray)
     */
    public static JSONArray buildInsertList(JSONArray existArray, String idsStr, JSONObject country) {
        JSONArray insertList = new JSONArray();

        // 1. 对字符串进行非空校验，如果为空则直接返回空集合
        if (StringUtils.isBlank(idsStr)) {
            return insertList;
        }

        // 2. 提取已有数组中的 countryId 放入 Set 中，提高比对效率 (O(1) 查找)
        Set<String> existIdSet = new HashSet<>();
        if (existArray != null && !existArray.isEmpty()) {
            for (int i = 0; i < existArray.size(); i++) {
                JSONObject obj = existArray.getJSONObject(i);
                if (obj != null && obj.containsKey("countryId")) {
                    // 统一转为 String 避免类型不一致导致的比对失败
                    existIdSet.add(String.valueOf(obj.get("countryId")));
                }
            }
        }

        // 3. 拆分字符串并处理
        String[] idArr = idsStr.split(",");
        for (String id : idArr) {
            String trimId = id.trim(); // 去除可能存在的前后空格
            if (StringUtils.isBlank(trimId)) {
                continue; // 跳过空串
            }

            // 4. 核心比对：如果不存在于已有集合中，则组装新对象
            if (!existIdSet.contains(trimId)) {
                JSONObject newItem = new JSONObject();

                // 如果 country 模板对象不为空，先复制其属性作为基础数据
                if (country != null) {
                    newItem.putAll(country);
                }

                // 覆盖/设置当前比对出的 countryId
                newItem.put("countryId", trimId);

                insertList.add(newItem);
            }
        }

        return insertList;
    }

    // ================= 测试用例 =================
    public static void main(String[] args) {
        // 1. 准备已有数据
        JSONArray existArray = new JSONArray();
        existArray.add(new JSONObject().fluentPut("countryId", "CN"));
        existArray.add(new JSONObject().fluentPut("countryId", "US"));

        // 2. 准备逗号分割的字符串 (包含已存在的 CN，不存在的 JP、KR，以及多余空格)
        String idsStr = "CN, JP , KR, US";

        // 3. 准备国家模板对象
        JSONObject countryTemplate = new JSONObject();
        countryTemplate.put("status", 1);
        countryTemplate.put("createBy", "system");

        // 4. 执行处理
        JSONArray result = buildInsertList(existArray, idsStr, countryTemplate);

        // 5. 输出结果
        System.out.println("需要插入的数据: " + result.toJSONString());
    }
}