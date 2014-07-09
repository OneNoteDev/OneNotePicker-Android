package com.microsoft.onenote.pickerlib;

class UiActionBarSettings {

        private Boolean showArrow;
        private String title;
        private String subtitle;

        protected UiActionBarSettings(String title, String subtitle, Boolean showArrow) {
            this.title = title;
            this.subtitle = subtitle;
            this.showArrow = showArrow;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public Boolean getShowArrow() {
            return showArrow;
        }

        public void setShowArrow(boolean showArrow) {
            this.showArrow = showArrow;
        }

}
