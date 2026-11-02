# 编译并运行自检（需要 JDK 17+，无需 Maven）
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
$files = Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out com.kdyz.station.StationCheck
