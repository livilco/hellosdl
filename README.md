# hellosdl
Hello, World! for Android &lt;> SDL Head unit

## Running the app 
- Visit https://smartdevicelink.com/resources/manticore/
- Click 'Launch Maintocore' button and wait for redirect
  - Sometimes fails and needs a browser refresh
- Replace `sdl_tcp_url` and `sdl_tcp_port` in `res/values/strings.xml`  with corresponding values from right-hand column in SDL emulator screen 
- Launch the `hellosdl` app in a phone emulator
  - App will automatically connect to the remote SDL emulator and the app tile should appear on screen
  - The app may launch automatically in the SDL emulator
  - Clicking 'Update View' in the phone emulator will trigger a template change in the SDL emulator
