> HKEY_LOCAL_MACHINE
>> SOFTWARE
>>> Microsoft
>>>> Windows
>>>>> CurrentVersion
>>>>>> Policies
>>>>>>> System
>>>>>>>> CredSSP
>>>>>>>>> Parameters
>>>>>>>>>> AllowEncryptionOracle,DWORD(32位),十六进制,2

#### 注册表文件示例
CredSSP.reg
```
WindowsRegistryEditorVer-sion5.00

[+HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Policies\System\CredSSP\Parameters]
"AllowEncryptionOracle"=dword:00000002
```
