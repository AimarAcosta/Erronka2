// APP.TS - Componente raíz de la aplicación Angular
// Carga el Navbar, el contenido dinámico (router-outlet) y el Footer

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { Footer } from './shared/footer/footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Footer, Navbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {
  title = 'ElorAdmin';
}
