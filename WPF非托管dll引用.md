# WPF非托管dll引用

### 引用方式
```
// exe当前目录
[DllImport(@".\HZSiInterface.dll")]
private static extern IntPtr INIT(StringBuilder result);

[DllImport(@".\HZSiInterface.dll", CallingConvention = CallingConvention.Winapi)]
private static extern IntPtr BUSINESS_HANDLE(string data, StringBuilder result);
```

### 参数回传
```
// 必须定义StringBuilder长度，否则会出现堆栈异常，导致内存读写冲突，程序崩溃
var result = new StringBuilder(4096);
var exec = BUSINESS_HANDLE(data, result);
callback(exec.ToInt32(), result);
```
