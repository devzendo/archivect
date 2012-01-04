package org.devzendo.archivect.filesystemaccess

import org.springframework.beans.factory.FactoryBean
import org.devzendo.xpfsa.FileSystemAccess


class FileSystemAccessFactory extends FactoryBean[FileSystemAccess] {
    var fsa: FileSystemAccess = null
    
    def getObject: FileSystemAccess = {
        return fsa
    }
    
    def getObjectType: Class[_] = {
        return classOf[FileSystemAccess]
    }
    
    def isSingleton: Boolean = {
        return true
    }
    
    def setFileSystemAccess(fileSystemAccess: FileSystemAccess): Unit = {
        fsa = fileSystemAccess
    }
}