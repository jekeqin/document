# WEB 打印 隐藏页眉页脚 URL 网址

### 增加以下 style 样式即可
```css
<!--		去除页眉页脚		-->
<style media="print">
@page {
    size: auto;  /* auto is the initial value */
    margin: 0mm; /* this affects the margin in the printer settings */
}
</style>
```
