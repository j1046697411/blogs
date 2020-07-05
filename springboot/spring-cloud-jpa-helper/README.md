# SpecificationHelper 使用注解快速构建spring-boot-starter-data-jpa查询语句

## 前言
&emsp;&emsp;本来我是一名Android开发人员不应该写这篇文章的，但是耐不住无聊在家，然后又对这一块
比较喜欢，所以才有了这个东西，而且我前面也写过一篇关于构造一个Android `RecyclerView`的
一个通用适配器，也不知道有没有用，唉，不废话了，还是进入我们今儿个的正题吧！  
&emsp;&emsp;想必大叫如果看过或者用过jpa的应该都会觉得如果查询多的话，会比较繁琐，而且每
个查询的代码又都差不多，只有条件的判断什么的有点不一样，所以从最初开始我就在网上看有没有能
减少一部分开发量的库，然而看了一圈还是没有找到比较合适的库可以使用，所以才有了今天这个工具。
## 目标
&emsp;&emsp;这次库的目标就是减少，增加查询修改`Repository`的次数,在最初的时候每次有新的
需求都需要对应的去添加service和Repository的代码。废话多说，直接上代码。
## 使用方式

### 1、模糊查询 @Keywords
&emsp;&emsp;先来看看Keywords的注解吧
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Keywords {

    /**
     * 需要查询的字段名，在Specification中称为path（路径）
     * @return
     */
    String[] value();

    /**
     * @see QueryGroup
     * 该查询的名称，方便我们后面QueryGroup做分组查询
     * @return
     */
    String name() default "";

    /**
     * 模糊查询的前缀，默认为 %
     * @return
     */
    String prefix() default "%";

    /**
     * 模糊查询的后缀，默认为 %
     * @return
     */
    String suffix() default "%";

    /**
     * @see Operator
     * 模糊查询连接条件 分为 AND 和 OR
     * @return
     */
    Operator operator() default Operator.OR;
}
```
&emsp;&emsp;查询条件代码
```java
@Data
public static class KeywordsQueryCriteria implements Serializable {

    /**
     * 模糊查询 account 和 token 两个字段，连接条件为 OR
     */
    @Keywords(value = {"account", "token"}, suffix = "%", prefix = "%", operator = Operator.OR)
    private String keywords;

}

```
&emsp;&emsp;查询生成的sql
```sql
SELECT
	accountent0_.id AS id1_0_,
	accountent0_.create_time AS create_t2_0_,
	accountent0_.is_delete AS is_delet3_0_,
	accountent0_.delete_time AS delete_t4_0_,
	accountent0_.update_time AS update_t5_0_,
	accountent0_.account AS account6_0_,
	accountent0_.ENABLE AS enable7_0_,
	accountent0_.locked AS locked8_0_,
	accountent0_.register_ip AS register9_0_,
	accountent0_.token AS token10_0_,
	accountent0_.type AS type11_0_ 
FROM
	tk_account accountent0_ 
WHERE
	accountent0_.account LIKE ? 
	OR accountent0_.token LIKE ?
```
&emsp;&emsp;看这样子是我们想要的结果，

### 2、单条属性查询 @Query

&emsp;&emsp;注解
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Queries.class)
public @interface Query {

    /**
     * @see QueryType
     * 查询类型
     * NOT_EQUAL 不相等
     * EQUAL 相等
     * GREATER_THAN 大于
     * GREATER_THAN_OR_EQUAL 大于等于
     * LESS_THAN 小于
     * LESS_THAN_OR_EQUAL 小于等于
     * IN 在集合或数组内 必须注解在 array 或者 Collection上
     * BETWEEN 在范围内 必须注解在 array 或者 List 上，并且元素要大于或等于2
     * @return
     */

    QueryType type();

    /**
     * @see QueryGroup
     * 该查询名称，方便我们后面QueryGroup做分组查询
     * @return 默认使用属性名
     */
    String name() default "";

    /**
     * 查询字段 path
     * @return 优先使用配置的路径，然后使用名称，最后使用属性名
     */
    String path() default "";
}
```
&emsp;&emsp;使用,这儿有一个需要特别注意的地方，`就是两个token的查询，需要给其中一个取名，不然会只有查询其中
一个`
```java
@Data
public static class QueryCriteria implements Serializable {

    /**
     * 查询范围内的id，这儿例子给的是0-20
     */
    @Query(type = QueryType.BETWEEN, path = "id")
    private Integer[] ids;

    /**
     * 查询在集合内的账号，这儿给的粒子是["123", "456", "789"]
     */
    @Query(type = QueryType.IN, path = "account")
    private String[] accounts;

    /**
     * 模糊查询token，给其查询取名为keyword_token，如果不取名默认为属性名和下面的查询冲突了
     */
    @Keywords(value = "token", name = "keyword_token")
    /**
     * 查询不为null的token，虽然没有实际意义，这儿只是给个例子，说明可以多次查同一字段
     */
    @Query(type = QueryType.IS_NULL, path = "token")
    private String token;

    /**
     * 查询delete条件
     */
    @Query(type = QueryType.EQUAL)
    private Boolean delete;
}
```
&emsp;&emsp;查询生成的sql
```sql
SELECT
	accountent0_.id AS id1_0_,
	accountent0_.create_time AS create_t2_0_,
	accountent0_.is_delete AS is_delet3_0_,
	accountent0_.delete_time AS delete_t4_0_,
	accountent0_.update_time AS update_t5_0_,
	accountent0_.account AS account6_0_,
	accountent0_.ENABLE AS enable7_0_,
	accountent0_.locked AS locked8_0_,
	accountent0_.register_ip AS register9_0_,
	accountent0_.token AS token10_0_,
	accountent0_.type AS type11_0_ 
FROM
	tk_account accountent0_ 
WHERE
	( accountent0_.token IS NULL ) 
	AND (
	accountent0_.token LIKE ?) 
	AND ( accountent0_.id BETWEEN 0 AND 20 ) 
	AND accountent0_.is_delete =?

```
&emsp;&emsp;查询使用的json
```json
{
    "ids":[0,20],
    "accounts":["123", "456", "789"],
    "token": "123",
    "delete": true
}
```
&emsp;&emsp;大家看到这儿是不是总觉得还差些什么？生成的sql，总是不能完全满足自己的要求，而且还不能控制多个查询之间的关系。
对，觉得差就对了，我们还差一个分组查询，能够任意组合我们几个查询之间关系的这么个东西，所以下面就出来了。

### 3、真正厉害的查询 @QueryGroup 分组查询（不是sql中 groupBy 查询，是把其中几个查询添加合并为一个）

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(QueryGroups.class)
public @interface QueryGroup {

    /**
     * 查询名称 可以把几个查询合并为一个查询，然后在用于其他的组
     * @return
     */
    String name();

    /**
     * 查询的目标名称
     * @return
     */
    String[] targetNames() default {};

    /**
     * 分组查询的条件 OR 或 AND
     * @return
     */
    Operator operator() default Operator.AND;

}

```

```java
@Data
@QueryGroup(name = "token_group", targetNames = {"token", "keyword_token"}, operator = Operator.AND)
@QueryGroup(name = "id_and_accounts", targetNames = {"ids", "accounts"}, operator = Operator.OR)
@QueryGroup(name = "root", targetNames = {"token_group", "id_and_accounts", "delete"}, operator = Operator.AND)
public static class QueryCriteria implements Serializable {

    /**
     * 查询范围内的id，这儿例子给的是0-20
     */
    @Query(type = QueryType.BETWEEN, path = "id")
    private Integer[] ids;

    /**
     * 查询在集合内的账号，这儿给的粒子是["123", "456", "789"]
     */
    @Query(type = QueryType.IN, path = "account")
    private String[] accounts;

    /**
     * 模糊查询token，给其查询取名为keyword_token，如果不取名默认为属性名和下面的查询冲突了
     */
    @Keywords(value = "token", name = "keyword_token")
    /**
     * 查询不为null的token，虽然没有实际意义，这儿只是给个例子，说明可以多次查同一字段
     */
    @Query(type = QueryType.IS_NULL, path = "token")
    private String token;

    /**
     * 查询delete条件
     */
    @Query(type = QueryType.EQUAL)
    private Boolean delete;
}
```
&emsp;&emsp;生成的sql
```sql
SELECT
	accountent0_.id AS id1_0_,
	accountent0_.create_time AS create_t2_0_,
	accountent0_.is_delete AS is_delet3_0_,
	accountent0_.delete_time AS delete_t4_0_,
	accountent0_.update_time AS update_t5_0_,
	accountent0_.account AS account6_0_,
	accountent0_.ENABLE AS enable7_0_,
	accountent0_.locked AS locked8_0_,
	accountent0_.register_ip AS register9_0_,
	accountent0_.token AS token10_0_,
	accountent0_.type AS type11_0_ 
FROM
	tk_account accountent0_ 
WHERE
	( accountent0_.token IS NULL ) 
	AND (
	accountent0_.token LIKE ?) 
	AND (
		accountent0_.id BETWEEN 0 
		AND 20 
		OR accountent0_.account IN (?,
			?,
		?)) 
	AND accountent0_.is_delete =?
```

## 实现代码

&emsp;&emsp;以下是全部的实现代码，有兴趣的盆友可以研究一下，现在这个东西还差一些东西，还有一些地方没有完善
在这儿只是抛砖引玉，希望大家能有更好的想法。

```java
public class SpecificationHelper {

    /**
     * 注解查询入口方法，使用注解的方式标记查询
     * @param queryCriteria 查询条件，带注解的对象
     * @param <T> 实体类型
     * @return Specification
     */
    public static <T> Specification<T> query(Object queryCriteria) {
        return (root, query, criteriaBuilder) -> {
            //构造方查询的map，key是查询的名字，value 是查询对象
            Map<String, Specification<T>> specifications = new HashMap<>();
            Class<?> type = queryCriteria.getClass();
            // 遍历满足条件的属性
            ReflectionUtils.doWithFields(type, field -> {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                //获取属性的值
                Object value = ReflectionUtils.getField(field, queryCriteria);
                if (ObjectUtils.isNull(value)) {
                    return;
                }
                // 判断属性是否是Keywords注解标记的属性
                if (hasAnnotation(field, Keywords.class)) {

                    //获取Keywords注解对象
                    Keywords[] keywords = field.getAnnotationsByType(Keywords.class);
                    //对象转换为string，like查询只能是spring
                    String keywordsValue = String.valueOf(value);
                    for (Keywords keyword : keywords) {
                        String name = keyword.name();
                        if (StringUtils.isEmpty(name)) {
                            name = field.getName();
                        }
                        //存入查询集合
                        specifications.put(name, queryKeywords(keyword, keywordsValue));
                    }
                }
                //创建查询集合
                List<Query> queries = new ArrayList<>();
                //判断是否有Query注解
                if (hasAnnotation(field, Query.class)) {
                    queries.add(AnnotationUtils.findAnnotation(field, Query.class));
                }
                //判断是否有Queries注解，内部存放的就是Query注解
                if (hasAnnotation(field, Queries.class)) {
                    queries.addAll(Arrays.asList(AnnotationUtils.findAnnotation(field, Queries.class).value()));
                }
                for (Query queryAnnotation : queries) {
                    String name = queryAnnotation.name();
                    if (StringUtils.isEmpty(name)) {
                        name = field.getName();
                    }
                    String path = queryAnnotation.path();
                    if (StringUtils.isEmpty(path)) {
                        path = name;
                    }
                    specifications.put(name, query(queryAnnotation, path, value));
                }
            }, field -> hasAnnotation(field, Keywords.class)
                    || hasAnnotation(field, Query.class)
                    || hasAnnotation(field, Queries.class));
            List<QueryGroup> queryGroups = new ArrayList<>();

            //判断class上注解有QueryGroup
            if (hasAnnotation(type, QueryGroup.class)) {
                queryGroups.add(AnnotationUtils.findAnnotation(type, QueryGroup.class));
            }
            if (hasAnnotation(type, QueryGroups.class)) {
                queryGroups.addAll(Arrays.asList(AnnotationUtils.findAnnotation(type, QueryGroups.class).value()));
            }
            Set<String> useKeys = new HashSet<>();
            for (QueryGroup queryGroup : queryGroups) {
                specifications.put(queryGroup.name(), queryGroup(queryGroup, specifications));
                useKeys.addAll(Arrays.asList(queryGroup.targetNames()));
            }
            Set<String> allKeys = new HashSet<>(specifications.keySet());
            System.out.println("allKeys:" + allKeys);
            System.out.println("useKeys:" + useKeys);
            Set<Predicate> predicates = allKeys.stream()
                    .filter(key -> !useKeys.contains(key))
                    .map(specifications::get)
                    .map(specification -> specification.toPredicate(root, query, criteriaBuilder))
                    .collect(Collectors.toSet());
            if (hasAnnotation(type, QueryRoot.class)) {
                return type.getAnnotation(QueryRoot.class).value().operation(criteriaBuilder, predicates);
            } else {
                return Operator.AND.operation(criteriaBuilder, predicates);
            }
        };
    }

    /**
     * 把 query 转换为对应的 Specification 查询对象
     * @param query
     * @param path
     * @param value
     * @param <T>
     * @return
     */
    private static <T> Specification<T> query(Query query, String path, Object value) {
        return (root, query1, criteriaBuilder) -> query.type().operation(root, criteriaBuilder, path, value);
    }

    /**
     * 把QueryGroup 注解转换为对应的Specification
     * @param queryGroup 分组查询对象
     * @param specifications 组内的查询对象 Specification
     * @param <T>
     * @return
     */
    private static <T> Specification<T> queryGroup(QueryGroup queryGroup, Map<String, Specification<T>> specifications) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String name : queryGroup.targetNames()) {
                if (specifications.containsKey(name)) {
                    predicates.add(specifications.get(name).toPredicate(root, query, criteriaBuilder));
                }
            }
            return queryGroup.operator().operation(criteriaBuilder, predicates);
        };
    }

    /**
     * 把 keywords 转换为对象的查询对象
     * @param keywords
     * @param value
     * @param <T>
     * @return
     */
    private static <T> Specification<T> queryKeywords(Keywords keywords, String value) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String keywordsValue = keywords.prefix() + value + keywords.suffix();
            for (String path : keywords.value()) {
                predicates.add(criteriaBuilder.like(root.get(path).as(String.class), keywordsValue));
            }
            return keywords.operator().operation(criteriaBuilder, predicates);
        };
    }

    private static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> type) {
        return element.getAnnotation(type) != null;
    }

}
```
&emsp;&emsp;IS_NOT_EMPTY 和 IS_EMPTY 查询还存在点问题，希望大家能改以改
```java
@SuppressWarnings({"unchecked", "all"})
public enum QueryType {
    NOT_EQUAL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.notEqual(root.get(path).as(value.getClass()), value);
        }
    }, EQUAL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.equal(root.get(path).as(value.getClass()), value);
        }
    }, GREATER_THAN {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.greaterThan(root.get(path), (Comparable) value);
        }
    }, GREATER_THAN_OR_EQUAL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(path), (Comparable) value);
        }
    }, LESS_THAN {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.lessThan(root.get(path), (Comparable) value);
        }
    }, LESS_THAN_OR_EQUAL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(path), (Comparable) value);
        }
    }, IS_NULL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.isNull(root.get(path));
        }
    }, IS_NOT_NULL {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.isNotNull(root.get(path));
        }
    }, IS_EMPTY {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.isEmpty(root.get(path));
        }
    }, IS_NOT_EMPTY {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            return criteriaBuilder.isNotEmpty(root.get(path));
        }
    }, IN {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            if (value instanceof Collection<?>) {
                return root.get(path).in((Collection<?>) value);
            } else if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                Object[] array = new Object[length];
                System.arraycopy(value, 0, array, 0, length);
                return root.get(path).in(array);
            } else {
                return root.get(path).in(value);
            }
        }
    }, BETWEEN {
        @Override
        public <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value) {
            Comparable min, max;
            if (value instanceof List<?>) {
                min = (Comparable) ((List<?>) value).get(0);
                max = (Comparable) ((List<?>) value).get(1);
            } else if (value.getClass().isArray()) {
                min = (Comparable) Array.get(value, 0);
                max = (Comparable) Array.get(value, 1);
            } else {
                throw new RuntimeException("value not array or List");
            }
            return criteriaBuilder.between(root.get(path), min, max);
        }
    };

    public abstract <T> Predicate operation(Root<T> root, CriteriaBuilder criteriaBuilder, String path, Object value);
}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryRoot {
    Operator value() default Operator.AND;
}
```

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryGroups {

    QueryGroup[] value() default {};
}
```

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Queries {
    Query[] value() default {};
}
```

```java
public enum Operator {
    OR {
        @Override
        public Predicate operation(CriteriaBuilder criteriaBuilder, Collection<Predicate> predicates) {
            return criteriaBuilder.or(predicates.toArray(PREDICATES));
        }
    }, AND {
        @Override
        public Predicate operation(CriteriaBuilder criteriaBuilder, Collection<Predicate> predicates) {
            return criteriaBuilder.and(predicates.toArray(PREDICATES));
        }
    };

    private static final Predicate[] PREDICATES = new Predicate[0];

    public abstract Predicate operation(CriteriaBuilder criteriaBuilder, Collection<Predicate> predicates);

}
```

## link
[作者](https://github.com/j1046697411)   
[CommonlyAdapter](https://blog.csdn.net/qq_19326641/article/details/106876369) 一个比较实用的Android `RecyclerViewAdapter`   
[spring-cloud-jpa-helper](https://github.com/j1046697411/blogs/tree/master/springboot/spring-cloud-jpa-helper) 源码所在  
[csdn 博客地址](https://blog.csdn.net/qq_19326641/article/details/107147117)  