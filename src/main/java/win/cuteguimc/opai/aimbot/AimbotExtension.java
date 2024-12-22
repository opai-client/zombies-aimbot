package win.cuteguimc.opai.aimbot;

import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;

// Required @ExtensionInfo annotation
@ExtensionInfo(name = "ZombiesAimbot",author = "unknown", version = "1.0")
public class AimbotExtension extends Extension {
    public static OpenAPI openAPI;

    @Override
    public void initialize(OpenAPI openAPI) {
        AimbotExtension.openAPI = openAPI;
    }
}
