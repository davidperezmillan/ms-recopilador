package com.davidperezmillan.recopilador.apllication.usecases;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScraperLauncherUseCase {

    CompletableFuture<List<String>>launchAllScrapers();
}
