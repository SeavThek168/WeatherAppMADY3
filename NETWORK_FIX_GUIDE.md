# Fix Android Emulator Network Issues

## Option 1: Restart Emulator with Custom DNS (Easiest)

1. **Close the current emulator completely**

2. **Run this command in PowerShell:**
```powershell
# Find your emulator name
cd $env:LOCALAPPDATA\Android\Sdk\emulator
.\emulator.exe -list-avds

# Then start emulator with Google DNS
.\emulator.exe -avd YOUR_AVD_NAME -dns-server 8.8.8.8,8.8.4.4
```

Replace `YOUR_AVD_NAME` with your emulator name (e.g., Pixel_5_API_34)

## Option 2: Check Windows Firewall

1. Open **Windows Security** → **Firewall & network protection**
2. Click **Allow an app through firewall**
3. Find **Android Studio** and **qemu-system** and make sure both Private and Public are checked
4. If not listed, click **Change settings** → **Allow another app** → Browse to:
   - `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`
   - `C:\Users\YOUR_USERNAME\AppData\Local\Android\Sdk\emulator\qemu-system-x86_64.exe`

## Option 3: Configure Emulator Network in Android Studio

1. **Wipe emulator data:**
   - Tools → Device Manager
   - Click dropdown on your emulator → **Wipe Data**
   - Restart emulator

2. **Check emulator settings:**
   - In running emulator, click **... (More)** button
   - Go to **Settings** → **Proxy**
   - Make sure it's set to **"Use Android Studio HTTP proxy settings"** or **"No proxy"**

## Option 4: Use Physical Device Instead

If emulator continues to have issues:
1. Enable **Developer Options** on your Android phone
2. Enable **USB Debugging**
3. Connect via USB
4. Run app on physical device (it will have internet!)

## Option 5: Test if it's DNS Issue

Run this in PowerShell to test if your computer can reach the API:
```powershell
curl "https://api.openweathermap.org/data/2.5/weather?q=London&appid=ffebb6d220be97c63c7cf84998a7af7f&units=metric"
```

If this works on your PC but not in emulator, it's definitely an emulator network configuration issue.

## Quick Test After Fix:

Once you've tried a fix:
1. Open Chrome browser in the emulator
2. Try visiting: google.com
3. If Chrome works, run the weather app test again
