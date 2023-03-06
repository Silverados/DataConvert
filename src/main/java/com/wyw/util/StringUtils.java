package com.wyw.util;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtils {
    public static String replaceTemplate(String template, Map<String, String> templateMap) {
        for (Map.Entry<String, String> entry : templateMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            var key = String.format("${%s}", entry.getKey());
            template = template.replace(key, entry.getValue());
        }
        return template;
    }

    public static String mergeString(String target, String source, Pattern[] patterns) {
        for (Pattern pattern : patterns) {
            var sourceMatcher = pattern.matcher(source);
            if (!sourceMatcher.find()) {
                continue;
            }
            var targetMatcher = pattern.matcher(target);
            if (!targetMatcher.find()) {
                continue;
            }
            var sourceStr = sourceMatcher.group();
            var templateStr = targetMatcher.group();
            target = target.replace(templateStr, sourceStr);
        }
        return target;
    }

    public static String lowerFirstCharacter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String upperFirstCharacter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
