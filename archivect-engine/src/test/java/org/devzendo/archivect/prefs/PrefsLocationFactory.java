/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.devzendo.archivect.prefs;

import java.io.File;

import org.devzendo.commonapp.prefs.DefaultPrefsLocation;
import org.springframework.beans.factory.FactoryBean;

/**
 * A PrefsLocation factory that allows the location of the prefs dir to be
 * injected.
 * 
 * @author matt
 *
 */
public final class PrefsLocationFactory implements FactoryBean<DefaultPrefsLocation> {
    private File mHomeDirectory;
    private String mPrefsSubDirName;
    private String mPrefsFileName;

    public void setDirectory(final File homeDirectory, final String prefsSubDirName, final String prefsFileName) {
        mHomeDirectory = homeDirectory;
        mPrefsSubDirName = prefsSubDirName;
        mPrefsFileName = prefsFileName;
    }

    public DefaultPrefsLocation getObject() throws Exception {
        return new DefaultPrefsLocation(mPrefsSubDirName, mPrefsFileName, mHomeDirectory.getAbsolutePath());
    }

    public Class<?> getObjectType() {
        return DefaultPrefsLocation.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
