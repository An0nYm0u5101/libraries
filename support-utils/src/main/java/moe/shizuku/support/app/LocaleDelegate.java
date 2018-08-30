package moe.shizuku.support.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.os.LocaleListCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class LocaleDelegate {

    /** current locales **/
    private static LocaleListCompat sDefaultLocales = LocaleListCompat.getDefault();

    /** system locales **/
    private static LocaleListCompat sSystemLocales = LocaleListCompat.getDefault();

    /** locale of this instance **/
    private LocaleListCompat mLocales = LocaleListCompat.getDefault();

    /**
     * Get default locale list stored in this class.
     *
     * @return default locale list
     */
    public static LocaleListCompat getDefaultLocale() {
        return sDefaultLocales;
    }

    /**
     * Set default locale, this will not call {@link Locale#setDefault(Locale)}.
     *
     * @param newLocale new locale
     */
    public static void setDefaultLocale(Locale newLocale) {
        final List<Locale> locales = new ArrayList<>();
        locales.add(newLocale);
        for (int index = 0; index < sSystemLocales.size(); index++) {
            final Locale item = sSystemLocales.get(index);
            if (!Objects.equals(newLocale, item)) {
                locales.add(item);
            }
        }
        sDefaultLocales = LocaleListCompat.create(locales.toArray(new Locale[0]));
    }

    /**
     * Get system locale stored in this class.
     *
     * @return system locale
     */
    public static LocaleListCompat getSystemLocale() {
        return sSystemLocales;
    }

    /**
     * Set system locale.
     *
     * @param systemLocale new locale
     */
    public static void setSystemLocales(LocaleListCompat systemLocale) {
        sSystemLocales = systemLocale;
    }

    /**
     * Return if current locale is different from default.
     * <p>Call this in {@link Activity#onResume()} and if true you should recreate activity.
     *
     * @return locale changed
     */
    public boolean isLocaleChanged() {
        return !Objects.equals(sDefaultLocales, mLocales);
    }

    /**
     * Update locale of given configuration, call in {@link Activity#attachBaseContext(Context)}.
     *
     * @param configuration Configuration
     */
    public void updateConfiguration(Configuration configuration) {
        mLocales = sDefaultLocales;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales((LocaleList) mLocales.unwrap());
        } else {
            configuration.setLocale(mLocales.get(0));
        }
    }

    /**
     * A dirty fix for wrong layout direction after switching locale between LTR and RLT language,
     * call in {@link Activity#onCreate(Bundle)}.
     *
     * @param activity Activity
     */
    public void onCreate(Activity activity) {
        Locale firstMatched = mLocales.getFirstMatch(activity.getAssets().getLocales());
        if (firstMatched == null) {
            firstMatched = mLocales.get(0);
        }
        activity.getWindow().getDecorView().setLayoutDirection(TextUtils.getLayoutDirectionFromLocale(firstMatched));
    }
}
