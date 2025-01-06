import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';
import { LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeEsCl from '@angular/common/locales/es-CL';

registerLocaleData(localeEsCl, 'es-CL');

bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [
    provideHttpClient(),
  ...appConfig.providers,
    {provide: LOCALE_ID, useValue: 'es-CL'},
  ]
})
  .catch((err) => console.error(err));