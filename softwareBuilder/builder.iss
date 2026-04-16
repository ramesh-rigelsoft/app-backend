[Setup]
AppName=RigelEIMS
AppVersion=1.0
DefaultDirName={autopf}\RigelEIMS
DefaultGroupName=RigelEIMS
OutputBaseFilename=RigelEIMS_Setup
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64compatible
SetupIconFile=C:\Users\Ramesh\Downloads\logoico.ico
PrivilegesRequired=admin
LicenseFile=license.txt

[Files]
Source: "D:\Java\todoapptest\target\todoapp.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot\*"; DestDir: "{app}\jdk"; Flags: recursesubdirs ignoreversion
Source: "D:\Java\todoapptest\softwareBuilder\run.bat"; DestDir: "{app}"; DestName: "run.bat"; Flags: ignoreversion
Source: "D:\Java\todoapptest\softwareBuilder\runSilently.vbs"; DestDir: "{app}"; DestName: "runSilently.vbs"; Flags: ignoreversion
Source: "D:\Java\todoapptest\softwareBuilder\startSilently.bat"; DestDir: "{app}"; DestName: "startSilently.bat"; Flags: ignoreversion
Source: "D:\Java\todoapptest\softwareBuilder\output\RigelLoader.exe"; DestDir: "{app}"; DestName: "RigelLoader.exe"; Flags: ignoreversion
Source: "D:\Java\todoapptest\todoapptest\resources\company\logo\logoico.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\RigelEIMS"; Filename: "{app}\RigelLoader.exe"; WorkingDir: "{app}"; IconFilename: "D:\Java\todoapptest\todoapptest\resources\company\logo\logoico.ico"
Name: "{autodesktop}\RigelEIMS"; Filename: "{app}\RigelLoader.exe"; WorkingDir: "{app}"; IconFilename: "D:\Java\todoapptest\todoapptest\resources\company\logo\logoico.ico"

[Run]
Filename: "{app}\RigelLoader.exe"; WorkingDir: "{app}"; Flags: nowait postinstall skipifsilent

[Code]
var
  LicensePage: TInputQueryWizardPage;  
  
function IsAlreadyInstalled(): Boolean;
begin
  Result := False; // default: not installed

   // 2️⃣ Registry check (recommended)
  // HKLM\Software\RigelEIMS\InstallPath
  if RegValueExists(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'LicenseKey') then
  begin
    Result := True;
    Exit;
  end;
end;
  
procedure InitializeWizard();
begin
  if IsAlreadyInstalled() then
  begin
    MsgBox('RigelEIMS is already installed on this PC.'#13#10 +
           'Setup will now exit.', mbInformation, MB_OK);
   Abort();
  end;
  
  LicensePage := CreateInputQueryPage(
    wpWelcome,
    'License Key Verification',
    'Enter your license key to continue installation',
    'Your key was emailed to you:'
  );
  LicensePage.Add('User Name:', False);
  LicensePage.Add('License Key:', False);
end;

function GetMacAddress(): String;
var
  Locator, WMIService, ColItems, Item: Variant;
  i: Integer;
begin
  Result := '';
  try
    Locator := CreateOleObject('WbemScripting.SWbemLocator');
    WMIService := Locator.ConnectServer('.', 'root\CIMV2');
    ColItems := WMIService.ExecQuery('SELECT * FROM Win32_NetworkAdapterConfiguration WHERE IPEnabled=TRUE');
    for i := 0 to ColItems.Count - 1 do
    begin
      Item := ColItems.Item(i);
      Result := Item.MACAddress;
      if Result <> '' then Exit;
    end;
  except
    Result := 'UNKNOWN';
  end;
end;

function VerifyLicenseOnline(UserName, SoftwareKey, MacAddr: String; ReqType: Boolean): Boolean;
var
  HTTP: Variant;
  JSONBody, ResponseText, CodeValue, Msg, ReqTypeStr: String;
  StartPos, EndPos: Integer;
begin
  Result := False;

  // Convert Boolean to JSON true/false
  if ReqType then
    ReqTypeStr := 'true'
  else
    ReqTypeStr := 'false';

  JSONBody := Format('{"username":"%s","softwareKey":"%s","macAddress":"%s","ReqType":%s}', [UserName, SoftwareKey, MacAddr, ReqTypeStr]);

  try
    HTTP := CreateOleObject('MSXML2.XMLHTTP');
    HTTP.Open('POST', 'http://10.58.248.141:8089/api/user/key/verify', False);
    HTTP.setRequestHeader('Content-Type', 'application/json');
    HTTP.Send(JSONBody);

    ResponseText := Trim(String(HTTP.responseText));

    // Extract code
    StartPos := Pos('"code":"', ResponseText);
  if StartPos > 0 then
  begin
    StartPos := StartPos + Length('"code":"');
    EndPos := StartPos;

    while (EndPos <= Length(ResponseText)) and (ResponseText[EndPos] <> '"') do
      EndPos := EndPos + 1;

    CodeValue := Copy(ResponseText, StartPos, EndPos - StartPos);
  end
  else
    CodeValue := '';

    // Extract message
    StartPos := Pos('"message":"', ResponseText);
    if StartPos > 0 then
    begin
      StartPos := StartPos + 11;
      EndPos := Pos('"', Copy(ResponseText, StartPos, Length(ResponseText)));
      if EndPos > 0 then
        Msg := Copy(ResponseText, StartPos, EndPos - 1)
      else
        Msg := '';
    end;

    if Trim(CodeValue) = '200' then
    begin
      Result := True;
      MsgBox('License Verified: ' + Msg, mbInformation, MB_OK);
    end
    else
    begin
      Result := False;
      MsgBox('License Verification Failed: ' + Msg, mbError, MB_OK);
    end;

  except
    MsgBox('Error connecting to license server. Check internet.', mbError, MB_OK);
    Result := False;
  end;
end;


procedure RegisterLicense(UserName, SoftwareKey: String);
begin
  RegWriteStringValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'UserName', UserName);
  RegWriteStringValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'LicenseKey', SoftwareKey);
end;

function NextButtonClick(CurPageID: Integer): Boolean;
var
  UserName, SoftwareKey, MacAddr: String;
  ReqType: Boolean;
begin
  Result := True;
  if CurPageID = LicensePage.ID then
  begin
    UserName := LicensePage.Values[0];
    SoftwareKey := LicensePage.Values[1];
    MacAddr := GetMacAddress();
    ReqType := True;

    if not VerifyLicenseOnline(UserName, SoftwareKey, MacAddr, ReqType) then
    begin
      MsgBox('Invalid license key! Installation cannot continue.', mbError, MB_OK);
      Result := False;
    end
    else
    begin
      RegisterLicense(UserName, SoftwareKey);
    end;
  end;
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
var
  UserName, SoftwareKey, MacAddr: String;
  ReqType: Boolean;
begin
  if CurUninstallStep = usUninstall then
  begin
    // Registry se values read karna
    if not RegQueryStringValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'UserName', UserName) then
      UserName := '';
    if not RegQueryStringValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'LicenseKey', SoftwareKey) then
      SoftwareKey := '';

    MacAddr := GetMacAddress();
    ReqType := False;

    if not VerifyLicenseOnline(UserName, SoftwareKey, MacAddr, ReqType) then
    begin
     // MsgBox('Uninstall license verification failed! Uninstall abort ho raha hai.', mbError, MB_OK);
      RaiseException('Uninstall blocked due to license verification failure.');
    end
    else
      if RegValueExists(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'UserName') then
        RegDeleteValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'UserName');
      if RegValueExists(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'LicenseKey') then
        RegDeleteValue(HKEY_LOCAL_MACHINE, 'Software\RigelEIMS', 'LicenseKey');
      MsgBox('Uninstall license verification success!', mbInformation, MB_OK);
  end;
end;
