/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.devzendo.archivect.gui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener

object SwingImplicits {
    /**
     * @param f the function to execute on receipt of an ActionEvent
     * @return an ActionListener
     */
    implicit def function2ActionListener(f: ActionEvent => Unit) = new ActionListener {
        def actionPerformed(event: ActionEvent) = f(event)
    }

    /**
     * @param f the function to execute on receipt of an ItemEvent
     * @return an ItemListener
     */
    implicit def function2ItemListener(f: ItemEvent => Unit) = new ItemListener {
        def itemStateChanged(event: ItemEvent) = f(event)
    }
}