package com.xvvvan.xhole.matchers;


import com.googlecode.aviator.AviatorEvaluator;
import com.xvvvan.xhole.Tuple;
import com.xvvvan.xhole.expression.Expressions;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static com.xvvvan.xhole.matchers.ConditionType.ORCondition;

//    matchers:
//          - type: word
//            part: body
//            words:
//            - '<title>3Com Corporation Web Interface</title>'
//            - '<frame name="mainFrame" src="blank.html">'
//            condition: and
//
//            - type: status
//            status:
//            - 200
//{
//        "name": "禅道 zentao",
//        "type": "word",
//        "part": "title",
//        "words": ["Welcome to zentao"]
//        }
//{
//        "name": "禅道 zentao",
//        "method": "keyword",
//        "location": "title",
//        "keyword": ["Welcome to zentao"]
//        }


//public MatcherTypeHolder type; 改掉了6.2
// Matcher is used to identify whether a template was successful.
public class Matcher {
    // Type is the type of the matcher
//    public MatcherTypeHolder type;
    public String type;

    public String condition;

    public String part;

    // description: |反转匹配
    //   Negative specifies if the match should be reversed
    //   It will only match if the condition is not true.
    public boolean negative;
    // Name is matcher Name to be displayed in result output.
    public String name;
    // Status are the acceptable status codes for the response
    public List<Integer> status;
    // Size is the acceptable size for the response
    public List<Integer> size;
    // Words are the words required to be present in the response
    public List<String> word;
    // Regex are the regex pattern required to be present in the response
    public List<String> regex;

    // Binary are the binary characters required to be present in the response
    public List<String> binary;
    // DSL are the dsl queries 需要引入第三方包 编译dsl语言 aviator规则引擎
    public List<String> dsl;

    // 编码方式
    public String encoding;
    // 是否启用不区分大小写的匹配
    public boolean case_insensitive;

    public boolean match_all;

    public ConditionType conditionType;
    // matcherType is the internal type of the matcher
    public MatcherType matcherType;
    public List<String> binaryDecoded;
    // regexCompiled is the compiled variant
    public List<Pattern> regexCompiled;

    public void compileMatchers() throws Exception {
        if(this.encoding!=null){
            if (this.encoding.equals("hex")) {
                for (int i = 0; i < this.word.size(); i++) {
                    String word = this.word.get(i);
                    byte[] decoded = Hex.decodeHex(word);
//                    byte[] decoded = DatatypeConverter.parseHexBinary(word);
                    if (decoded != null && decoded.length > 0) {
                        this.word.set(i, new String(decoded, StandardCharsets.UTF_8));
                    }
                }
            }
        }

        this.matcherType = MatcherType.fromValue(type);
        if(part==null){
            part = "body";
        }
        //compile the regexes
        if (regex != null && !regex.isEmpty()){
            for (String s : regex) {
                try {
                    Pattern compile = Pattern.compile(s);
                    this.regexCompiled.add(compile);
                }catch (Exception e){
                    System.out.println("could not compile regex: %s"+ regex+e.getMessage());
                }
            }
        }
        if(binary!=null&& !binary.isEmpty()){
            this.binaryDecoded = new ArrayList<>();
            for (String value : this.binary) {
                byte[] decoded = Hex.decodeHex(value);
                if (decoded != null && decoded.length > 0) {
                    binaryDecoded.add(new String(decoded, StandardCharsets.UTF_8));
                }
            }
        }
        // Setup the condition type, if any.
        if (condition!=null){
            ConditionType conditionType = ConditionType.fromValue(condition);
            if(conditionType!=null){
                this.conditionType = conditionType;
            }
        }else {
            this.conditionType = ORCondition;
        }
        if(this.case_insensitive){
            if(this.getMatcherType()!=MatcherType.WordsMatcher){
                throw new Exception("case-insensitive flag is supported only for 'word' matchers (not '%s')"+this.getMatcherType());
            }
            for (int i = 0; i < this.word.size(); i++) {
                String s = this.word.get(i);
                this.word.set(i,s.toLowerCase(Locale.ROOT));
            }
        }

    }
//
    public boolean matchDSL(Map<String,Object> data) {
//        Map<String, Object> env = AviatorEvaluator.newEnv("body", body, "headers", headers);
        for (int i = 0; i < this.dsl.size(); i++) {
            Object execute = AviatorEvaluator.execute(dsl.get(i), data);
            Boolean boolResult = (Boolean) execute;
            if(!boolResult){
                switch (conditionType){
                    case ANDCondition:
                        return false;
                    case ORCondition:
                        continue;
                }
                continue;
            }
            if (this.conditionType == ORCondition){
                return true;
            }
            if(this.dsl.size()-1 == i){
                return true;
            }
        }
        return false;
    }

    public Tuple<Boolean,List<String>> matchBinary(String corpus) {
        // Iterate over all the words accepted as valid
        ArrayList<String> matchedBinary = new ArrayList<>();
        for (int i = 0; i < this.binaryDecoded.size(); i++) {
            String binary = this.binaryDecoded.get(i);
            if(!corpus.contains(binary)){
                switch (conditionType){
                    case ANDCondition:
                        return new Tuple<Boolean,List<String>>(false,null);
                    case ORCondition:
                        continue;
                }
            }
            if (conditionType == ORCondition){
                matchedBinary.add(binary);
                return new Tuple<Boolean,List<String>>(true,matchedBinary);
            }
            matchedBinary.add(binary);
            if(binaryDecoded.size()-1==i){
                return new Tuple<Boolean,List<String>>(true,matchedBinary);
            }
        }
        return new Tuple<Boolean,List<String>>(false,null);

    }

    public Tuple<Boolean,List<String>> matchRegex(String corpus) {
        List<String> matchedRegexes = new ArrayList<String>();

        for (int i = 0; i < this.regexCompiled.size(); i++) {
            Pattern pattern = regexCompiled.get(i);
            // Continue if the regex doesn't match
            java.util.regex.Matcher matcher = pattern.matcher(corpus);
            if (!matcher.find()) {
                switch (conditionType) {
                    case ANDCondition:
                        return new Tuple<Boolean,List<String>>(false,null);
                    case ORCondition:
                        continue;
                }
            }
            List<String> currentMatches = new ArrayList<String>();
            do {
                currentMatches.add(matcher.group());
            } while (matcher.find());

            if (conditionType == ORCondition && !match_all) {
                return new Tuple<Boolean,List<String>>(true,currentMatches);
            }

            matchedRegexes.addAll(currentMatches);

            if (i == regexCompiled.size() - 1 && !match_all) {
                return new Tuple<Boolean,List<String>>(true,matchedRegexes);
            }
        }

        if (!matchedRegexes.isEmpty() && match_all) {
            return new Tuple<Boolean,List<String>>(true,matchedRegexes);
        }

        return new Tuple<Boolean,List<String>>(false,null);
    }

    public Tuple<Boolean,List<String>> matchWords(String corpus, Map<String, Object> data) {

        if (this.case_insensitive) {
            corpus = corpus.toLowerCase(Locale.ROOT);
        }
        List<String> matchedWords = new ArrayList<>();
        // Iterate over all the words accepted as valid
        for (int i = 0; i < this.word.size(); i++) {
            if(data==null){
                data = new HashMap<>();
            }
            String word = this.word.get(i);

            // 如果需要进行表达式求值，则用数据替换变量

            try {
                word = Expressions.evaluate(word, data);
            } catch(Exception e) {
                System.out.printf("Error while evaluating word matcher: %s", word);
                if (this.conditionType == ConditionType.ANDCondition) {
                    return new Tuple<>(false,null);
                }
            }

            if(!corpus.contains(word)){
                switch (this.conditionType){
                    case ANDCondition:
                        return new Tuple<>(false,null);
                    case ORCondition:
                        continue;
                }
            }
            if(this.conditionType==ConditionType.ORCondition && !this.match_all){
                matchedWords.add(word);
                return new Tuple<>(true, matchedWords);
            }
            matchedWords.add(word);
            if(this.word.size()-1==i&&!this.match_all){
                return new Tuple<>(true, matchedWords);
            }

        }
        if(!matchedWords.isEmpty() &&this.match_all){
            return new Tuple<>(true, matchedWords);
        }
        return new Tuple<>(false, matchedWords);
    }

    public boolean matchStatusCode(int statusCode) {
        // Iterate over all the status codes accepted as valid
        //
        // Status codes don't support AND conditions.
        for (Integer s : this.status) {
            // Continue if the status codes don't match
            if(s != statusCode){
                continue;
            }
            // Return on the first match.
            return true;
        }
        return false;
    }

    public boolean matchSize(int length) {
        // Iterate over all the sizes accepted as valid
        // Sizes codes don't support AND conditions.
        for (Integer s : this.size) {
            // Continue if the size doesn't match
            if(length!=s){
                continue;
            }
            // Return on the first match.
            return true;
        }
        return false;
    }

    public static final List<String> commonExpectedFields = Arrays.asList("Type", "Condition", "Name", "match_all", "Negative");

//    public void validate() throws Exception { // uses yaml marshaling to convert the struct to map[string]interface to have same field names
//         Map<String, Object> matcherMap = new HashMap<String, Object>();
//         Yaml yaml = new Yaml();
//         String marshaledMatcher = yaml.dump(this);
//         matcherMap = yaml.load(marshaledMatcher);
//
//        List<String> expectedFields = new ArrayList<String>();
//        switch (getMatcherType()) {
//            case DSLMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.add("DSL");
//                break;
//            case StatusMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.addAll(Arrays.asList("Status", "Part"));
//                break;
//            case SizeMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.addAll(Arrays.asList("Size", "Part"));
//                break;
//            case WordsMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.addAll(Arrays.asList("Words", "Part", "Encoding", "CaseInsensitive"));
//                break;
//            case BinaryMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.addAll(Arrays.asList("Binary", "Part", "Encoding", "CaseInsensitive"));
//                break;
//            case RegexMatcher:
//                expectedFields.addAll(commonExpectedFields);
//                expectedFields.addAll(Arrays.asList("Regex", "Part", "Encoding", "CaseInsensitive"));
//                break;
//        }
//        checkFields(this, matcherMap, expectedFields.toArray(new String[0]));
//    }

    public static void checkFields(Matcher m, Map<String, Object> matcherMap, String... expectedFields) throws Exception {
        List<String> foundUnexpectedFields = new ArrayList<String>();
        for (String marshaledFieldName : matcherMap.keySet()) {
            // revert back the marshaled name to the original field
            String structFieldName = getFieldNameFromYamlTag(marshaledFieldName, m);
            if (!Arrays.asList(expectedFields).contains(structFieldName)) {
                foundUnexpectedFields.add(structFieldName);
            }
        }
        if (!foundUnexpectedFields.isEmpty()) {
//            throw new Exception(String.format("matcher %s has unexpected fields: %s", m.getMatcherType(), String.join(",", foundUnexpectedFields)));
        }
    }
    public static String getFieldNameFromYamlTag(String tagName, Object object) throws Exception {
//        Class<?> reflectType = object.getClass();
//        if (!reflectType.isAnnotationPresent(YamlProperty.class) || !Modifier.isPublic(reflectType.getModifiers())) {
//            throw new Exception("the object must be a public class with @YamlProperty annotation");
//        }
//        for (Field field : reflectType.getDeclaredFields()) {
//            YamlProperty yamlProp = field.getAnnotation(YamlProperty.class);
//            if (yamlProp != null && yamlProp.value().equals(tagName)) {
//                return field.getName();
//            }
//        }
//        throw new Exception(String.format("field %s not found in class %s", tagName, reflectType.getSimpleName())); }
        return "";
    }
//    public boolean result(boolean data) { if (this.isNegative()) { return !data; } return data; }
////    boolean data, List<String> matchedSnippet
//    public Tuple<Boolean, List<String>> resultWithMatchedSnippet(Tuple<Boolean,List<String>> tuple) {
//        Boolean data = tuple.getFirst();
//        List<String> matchedSnippet = tuple.getSecond();
//        if (this.isNegative()) {
//            return new Tuple<>(!data, new ArrayList<>()); }
//        return new Tuple<>(data, matchedSnippet); }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public List<Integer> getSize() {
        return size;
    }

    public void setSize(List<Integer> size) {
        this.size = size;
    }

    public List<String> getWord() {
        return word;
    }

    public void setWord(List<String> word) {
        this.word = word;
    }

    public List<String> getRegex() {
        return regex;
    }

    public void setRegex(List<String> regex) {
        this.regex = regex;
    }

    public List<String> getBinary() {
        return binary;
    }

    public void setBinary(List<String> binary) {
        this.binary = binary;
    }

    public List<String> getDsl() {
        return dsl;
    }

    public void setDsl(List<String> dsl) {
        this.dsl = dsl;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isCase_insensitive() {
        return case_insensitive;
    }

    public void setCase_insensitive(boolean case_insensitive) {
        this.case_insensitive = case_insensitive;
    }

    public boolean isMatch_all() {
        return match_all;
    }

    public void setMatch_all(boolean match_all) {
        this.match_all = match_all;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public MatcherType getMatcherType() {
        return matcherType;
    }

    public void setMatcherType(MatcherType matcherType) {
        this.matcherType = matcherType;
    }

    public List<String> getBinaryDecoded() {
        return binaryDecoded;
    }

    public void setBinaryDecoded(List<String> binaryDecoded) {
        this.binaryDecoded = binaryDecoded;
    }

    public List<Pattern> getRegexCompiled() {
        return regexCompiled;
    }

    public void setRegexCompiled(List<Pattern> regexCompiled) {
        this.regexCompiled = regexCompiled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matcher: {");
        if (type != null)
            sb.append(" type: ").append(type);
        if (condition != null)
            sb.append(", condition: ").append(condition);
        if (part != null)
            sb.append(", part: ").append(part);
        sb.append(", negative: ").append(negative);

        if (name != null)
            sb.append(", name: ").append(name);
        if (status != null)
            sb.append(", status: ").append(status);
        if (size != null)
            sb.append(", size: ").append(size);
        if (word != null)
            sb.append(", word: ").append(word);
        if (regex != null)
            sb.append(", regex: ").append(regex);
        if (binary != null)
            sb.append(", binary: ").append(binary);
        if (dsl != null)
            sb.append(", dsl: ").append(dsl);
        if (encoding != null)
            sb.append(", encoding: ").append(encoding);

        sb.append(", case_insensitive: ").append(case_insensitive);

        sb.append(", match_all: ").append(match_all);

        if (conditionType != null)
            sb.append(", conditionType: ").append(conditionType);
        if (matcherType != null)
            sb.append(", matcherType: ").append(matcherType);
        if (binaryDecoded != null)
            sb.append(", binaryDecoded: ").append(binaryDecoded);
//        if (regexCompiled != null)
//            sb.append(", regexCompiled: ").append(regexCompiled);
//        if (dslCompiled != null)
//            sb.append(", dslCompiled: ").append(dslCompiled);
        sb.append(" }");
        return sb.toString();
    }
}
