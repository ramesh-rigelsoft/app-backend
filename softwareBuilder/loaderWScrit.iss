[Setup]
AppName=RigelEIMS
AppVersion=1.0
DefaultDirName={app}
OutputBaseFilename=RigelLoader
DisableStartupPrompt=yes
DisableDirPage=yes
DisableProgramGroupPage=yes
DisableReadyPage=yes
DisableFinishedPage=yes
DisableWelcomePage=yes
Uninstallable=no
CreateAppDir=no
PrivilegesRequired=admin
; OutputDir=.

[Files]
; Source: "D:\Java\todoapptest\runner.bat"; DestDir: "{tmp}"; Flags: deleteafterinstall
; Source: "D:\Java\todoapptest\runner.bat"; DestDir: "D:\runs"
; Source: "D:\Java\todoapptest\run_silently.vbs"; DestDir: "{app}"; Flags: ignoreversion

[Code]
var
  LoaderForm: TForm;
  SplashLabel: TLabel;
  ProgressPanel: TPanel;
  ProgressContainer: TPanel;


procedure ShowLoader();
begin
  LoaderForm := TForm.Create(nil);
  LoaderForm.Caption := 'RigelEIMS';
  LoaderForm.Width := 500;
  LoaderForm.Height := 250;
  LoaderForm.Position := poScreenCenter;

  // Splash label
  SplashLabel := TLabel.Create(LoaderForm);
  SplashLabel.Parent := LoaderForm;
  SplashLabel.Caption := 'Starting RigelEIMS... Please wait.';
  SplashLabel.Left := 50;
  SplashLabel.Top := 50;
  SplashLabel.Width := 400;
  SplashLabel.Alignment := taCenter;

  // Container for progress
  ProgressContainer := TPanel.Create(LoaderForm);
  ProgressContainer.Parent := LoaderForm;
  ProgressContainer.Left := 50;
  ProgressContainer.Top := 150;
  ProgressContainer.Width := 400;
  ProgressContainer.Height := 20;
  ProgressContainer.BevelOuter := bvLowered;

  // Moving panel representing progress
  ProgressPanel := TPanel.Create(LoaderForm);
  ProgressPanel.Parent := ProgressContainer;
  ProgressPanel.Left := 0;
  ProgressPanel.Top := 0;
  ProgressPanel.Height := ProgressContainer.Height;
  ProgressPanel.Width := 0;
  ProgressPanel.Color := clBlue;

  LoaderForm.Show;
  LoaderForm.Update;
end;

// Function to update simulated progress
procedure UpdateProgress(Percent: Integer);
begin
  ProgressPanel.Width := ProgressContainer.Width * Percent div 100;
  LoaderForm.Update;
end;

function InitializeSetup(): Boolean;
var
  ResultCode: Integer;
  I: Integer;
  LogPath: string;
begin
  Result := True;

  // Show loader
  ShowLoader();

  LogPath := ExpandConstant('C:\Program Files\RigelEIMS\startlog.txt');

  // Start BAT async
  Exec('cmd.exe',
       '/C "C:\Program Files\RigelEIMS\startSilently.bat"',
       '',
       SW_HIDE,
       ewNoWait,
       ResultCode);

  // Progress loop
  I := 0;
  while I <= 100 do
  begin
    UpdateProgress(I);
    Sleep(300);

    if FileExists(LogPath) then
    begin
      // File detected → hide loader but DO NOT free yet
      if Assigned(LoaderForm) then
        LoaderForm.Hide;

      Break;
    end;

    Inc(I);
  end;

  // Force complete progress
  UpdateProgress(100);

  // Update splash label safely
  if Assigned(SplashLabel) then
    SplashLabel.Caption := 'Completed ✅';

  // Give user a moment
  if Assigned(LoaderForm) then
  begin
    LoaderForm.Update;
    Sleep(1000);

    // Free LoaderForm only AFTER all operations
    LoaderForm.Free;
  end;

  Result := False;
end;