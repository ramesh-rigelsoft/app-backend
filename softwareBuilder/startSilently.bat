REM @echo off
REM cd /c "%~dp0"

REM wscript.exe "%~dp0runSilently.vbs"


@echo off

for /f "tokens=1" %%d in ('wmic logicaldisk get name ^| find ":"') do (
    if exist "%%d\runSilently.vbs" (
        echo Running on %%d
        wscript.exe "%%d\runSilently.vbs"
    )
)