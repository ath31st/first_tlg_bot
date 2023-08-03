package org.example.botfarm.service

interface ServiceFactory {
    fun makeService(nameService: String): Service
}