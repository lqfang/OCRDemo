package com.xinhuo.ocrdemo.entity;

import java.util.List;

public class WordsResult{

    /**
     * words_result_num : 1
     * words_result : [{"locale":{"right_bottom":[2227,1591],"left_top":[635,685]},"words":" TinMEal"}]
     */

    private int errCode;
    private String errInfo;
    private int words_result_num;
    private List<WordsResultBean> words_result;

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

    public List<WordsResultBean> getWords_result() {
        return words_result;
    }

    public void setWords_result(List<WordsResultBean> words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * locale : {"right_bottom":[2227,1591],"left_top":[635,685]}
         * words :  TinMEal
         */

        private LocaleBean locale;
        private String words;

        public LocaleBean getLocale() {
            return locale;
        }

        public void setLocale(LocaleBean locale) {
            this.locale = locale;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public static class LocaleBean {
            private List<Integer> right_bottom;
            private List<Integer> left_top;

            public List<Integer> getRight_bottom() {
                return right_bottom;
            }

            public void setRight_bottom(List<Integer> right_bottom) {
                this.right_bottom = right_bottom;
            }

            public List<Integer> getLeft_top() {
                return left_top;
            }

            public void setLeft_top(List<Integer> left_top) {
                this.left_top = left_top;
            }

            @Override
            public String toString() {
                return "LocaleBean{" +
                        "right_bottom='" + right_bottom + '\'' +
                        ", left_top='" + left_top + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "WordsResultBean{" +
                    "locale='" + locale + '\'' +
                    ", words='" + words + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WordsResult{" +
                "words_result_num='" + words_result_num + '\'' +
                ", words_result='" + words_result + '\'' +
                '}';
    }
}
