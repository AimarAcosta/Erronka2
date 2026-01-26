using Android.App;
using Android.Runtime;

namespace ElorMAUI
{
    // Añado esto porque somo usamos http nos lo puede bloquear por no ser https
    [Application(UsesCleartextTraffic = true)]
    public class MainApplication : MauiApplication
    {
        public MainApplication(IntPtr handle, JniHandleOwnership ownership)
            : base(handle, ownership)
        {
        }

        protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();
    }
}