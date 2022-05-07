package org.example.botfarm.service;

interface ServiceFactory {
    Service makeService(String nameService);
}
