using ElorMAUI.Services;
using Microsoft.Extensions.Logging;

namespace ElorMAUI
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var builder = MauiApp.CreateBuilder();
            builder
                .UseMauiApp<App>()
                .ConfigureFonts(fonts =>
                {
                    fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                });

            builder.Services.AddMauiBlazorWebView();

#if DEBUG
            builder.Services.AddBlazorWebViewDeveloperTools();
            builder.Logging.AddDebug();
#endif
            builder.Services.AddSingleton<CentroService>();
            builder.Services.AddSingleton<SesionService>();

            // Para hacer las peticiones a la API
            builder.Services.AddScoped(sp => new HttpClient());

            return builder.Build();
        }
    }
}