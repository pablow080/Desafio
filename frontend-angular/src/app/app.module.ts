import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { AppRoutingModule } from './app-routing.module';
import { ClientesModule } from './clientes/clientes.module';
import { SharedModule } from './shared/shared.module';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MAT_DATE_FORMATS, MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DatePipe } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';

// Formato de data para yyyy-MM-dd
const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'yyyy-MM-dd',  // Este formato é usado para parsing de input de data
  },
  display: {
    dateInput: 'yyyy-MM-dd',  // Este formato é usado para exibir a data no input
    monthYearLabel: 'MMM yyyy', // Exibição no seletor de mês e ano
    dateA11yLabel: 'LL',  // Formato de acessibilidade
    monthYearA11yLabel: 'MMMM yyyy', // Formato de acessibilidade para mês/ano
  }
};

@NgModule({
  declarations: [
    // Se você tiver componentes específicos, adicione aqui
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    AppRoutingModule,
    ClientesModule,
    SharedModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatOptionModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  providers: [
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    DatePipe  // Adicione o DatePipe aos providers
  ],
  bootstrap: []
})
export class AppModule { }
