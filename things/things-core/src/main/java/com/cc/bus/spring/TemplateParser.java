package com.cc.bus.spring;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.util.Arrays;
import java.util.function.Function;

/**
 * 解析模板数据
 * wcc 2022/6/9
 */
@Slf4j
public class TemplateParser {
    private static final char[] DEFAULT_PREPARE_START_SYMBOL = "${".toCharArray();

    private static final char[] DEFAULT_PREPARE_END_SYMBOL = "}".toCharArray();

    @Getter
    @Setter
    private char[] prepareStartSymbol = DEFAULT_PREPARE_START_SYMBOL;

    @Getter
    @Setter
    private char[] prepareEndSymbol = DEFAULT_PREPARE_END_SYMBOL;

    @Getter
    @Setter
    private String template;

    @Getter
    @Setter
    private Object parameter;

    private char[] templateArray;

    private int pos;

    private char symbol;

    private char[] newArr;

    private int len = 0;

    private byte prepareFlag = 0;

    public void setParsed(char[] chars, int end) {
        for (int i = 0; i < end; i++) {
            char aChar = chars[i];
            if (newArr.length <= len) {
                newArr = Arrays.copyOf(newArr, len + templateArray.length);
            }
            newArr[len++] = aChar;
        }

    }

    public void setParsed(char... chars) {
        setParsed(chars, chars.length);
    }

    private void init() {
        templateArray = template.toCharArray();
        pos = 0;
        newArr = new char[templateArray.length * 2];
    }

    private boolean isPreparing() {
        return prepareFlag > 0;
    }

    private boolean isPrepare() {
        if (prepareStartSymbol[prepareFlag] == symbol) {
            prepareFlag++;
        }
        if (prepareFlag >= prepareStartSymbol.length) {
            prepareFlag = 0;
            return true;
        }
        return false;
    }

    private boolean isPrepareEnd() {
        for (char c : prepareEndSymbol) {
            if (c == symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean next() {
        symbol = templateArray[pos++];
        return pos < templateArray.length;
    }

    public String parse(Function<String, String> propertyMapping) {
        init();
        boolean inPrepare = false;

        char[] expression = new char[128];
        int expressionPos = 0;

        while (next()) {
            if (isPrepare()) {
                inPrepare = true;
            } else if (inPrepare && isPrepareEnd()) {
                inPrepare = false;
                setParsed(propertyMapping.apply(new String(expression, 0, expressionPos)).toCharArray());
                expressionPos = 0;
            } else if (inPrepare) {
                if (expression.length <= expressionPos) {
                    expression = Arrays.copyOf(expression, (int) (expression.length * 1.5));
                }
                expression[expressionPos++] = symbol;
            } else if (!isPreparing()) {
                setParsed(symbol);
            }
        }

        if (isPrepareEnd() && expressionPos > 0) {
            setParsed(propertyMapping.apply(new String(expression, 0, expressionPos)).toCharArray());
        } else {
            setParsed(symbol);
        }

        return new String(newArr, 0, len);
    }


    public static String parse(String template, Object parameter) {
        return parse(template, var -> {

            try {
                return BeanUtilsBean.getInstance().getProperty(parameter, var);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            return "";
        });
    }

    public static String parse(String template, Function<String, String> parameterGetter) {
        TemplateParser parser = new TemplateParser();
        parser.template = template;
        return parser.parse(parameterGetter);
    }
}
