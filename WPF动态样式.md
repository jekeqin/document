# WPF动态样式

#### 动态创建样式并绑定
```c#
Style style = new Style();
style.TargetType = typeof(Label);
style.Setters.Add(new Setter(Control.FontSizeProperty, (double)App.Config.字体大小));
style.Setters.Add(new Setter(Control.ForegroundProperty, new SolidColorBrush(Color.FromRgb(90, 90, 90))));
style.Setters.Add(new Setter(Control.VerticalAlignmentProperty, VerticalAlignment.Center));
//this.Resources.Add(typeof(Label), style);
this.Resources[typeof(Label)] = style;
```

#### 获取当前样式
```c#
Style style = (Style)this.FindResource("screen1366");
```

#### 获取资源
```xml
<Window.Resources>
    <entity:EnableNotify x:Key="enable" input="True"/>
</Window.Resources>
```
```c#
EnableNotify obj = (EnableNotify)this.FindResource("enable");
```

#### 设置样式
```c#
element.Style = style;
```


### 动态修改元素Grid.Column
```c#
Grid.SetColumn(element, 1);
```
#### 动态修改Grid.Column宽度
```c#
<ColumnDefinition x:Name="col" Width="1*"/>
```
```c#
col.Width = new GridLength(3, GridUnitType.Star);
```
