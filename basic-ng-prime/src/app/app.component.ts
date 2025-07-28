import { Component } from '@angular/core';
import { ButtonDemoComponent } from './shared/components/button-demo/button-demo.component';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-root',
  imports: [ButtonDemoComponent, ButtonModule, ProgressSpinnerModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'basic-ng-prime';
}
