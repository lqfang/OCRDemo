package com.xinhuo.ocrdemo.entity;

import java.util.List;

public class Words {

    /**
     * words_result_num : 5
     * words_result : {"公司代码":"RAWU","集装箱编号":"210063","校验码识别":"6","校验码计算":"6","其他":"22G1"}
     */

    private int errCode;
    private String errInfo;
    private int words_result_num;
    private WordsResultBean words_result;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }


    public int getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(int words_result_num) {
        this.words_result_num = words_result_num;
    }

    public WordsResultBean getWords_result() {
        return words_result;
    }

    public void setWords_result(WordsResultBean words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * 公司代码 : RAWU
         * 集装箱编号 : 210063
         * 校验码识别 : 6
         * 校验码计算 : 6
         * 其他 : 22G1
         */

        private String 公司代码;
        private String 集装箱编号;
        private String 校验码识别;
        private String 校验码计算;
        private String 其他;

        public String get公司代码() {
            return 公司代码;
        }

        public void set公司代码(String 公司代码) {
            this.公司代码 = 公司代码;
        }

        public String get集装箱编号() {
            return 集装箱编号;
        }

        public void set集装箱编号(String 集装箱编号) {
            this.集装箱编号 = 集装箱编号;
        }

        public String get校验码识别() {
            return 校验码识别;
        }

        public void set校验码识别(String 校验码识别) {
            this.校验码识别 = 校验码识别;
        }

        public String get校验码计算() {
            return 校验码计算;
        }

        public void set校验码计算(String 校验码计算) {
            this.校验码计算 = 校验码计算;
        }

        public String get其他() {
            return 其他;
        }

        public void set其他(String 其他) {
            this.其他 = 其他;
        }

        @Override
        public String toString() {
            return "WordsResultBean{" +
                    "公司代码='" + 公司代码 + '\'' +
                    ", 集装箱编号='" + 集装箱编号 + '\'' +
                    ", 校验码识别='" + 校验码识别 + '\'' +
                    ", 校验码计算='" + 校验码计算 + '\'' +
                    ", 其他='" + 其他 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Words{" +
                "errCode=" + errCode +
                ", errInfo='" + errInfo + '\'' +
                ", words_result_num=" + words_result_num +
                ", words_result=" + words_result +
                '}';
    }
}
