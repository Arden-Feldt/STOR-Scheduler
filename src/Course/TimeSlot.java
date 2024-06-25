package Course;

public enum TimeSlot {
    MWF800850(),MWF905955,MWF10101100,MWF11151205,MWF1220110,MWF125215,MWF230320,MWF545635,
    TTH800915,TTH9301045,TTH11001215(true),TTH1230145(true),TTH200315(true),TTH330445,TTH500615,MW9051020,
    MW11151230,MW125240;

    private final boolean highRequest;

    TimeSlot(){this.highRequest = false;}

    // TODO: Make sure i have the right highRequest times

    TimeSlot(boolean highRequest) {
        this.highRequest = highRequest;
    }
}
