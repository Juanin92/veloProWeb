import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { UserService } from '../../services/User/user.service';
import { User } from '../../models/Entity/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserValidator } from '../../validation/user-validator';
import { Role } from '../../models/enum/role';
import { TooltipService } from '../../utils/tooltip.service';
import { NotificationService } from '../../utils/notification-service.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit, AfterViewInit {

  userList: User[] = [];
  addUserButton: boolean = true;
  showForm: boolean = false;
  existingMasterUser: boolean = true;
  validator = UserValidator;
  roles: string[] = Object.values(Role);
  user: User;
  selectedUser: User | null = null;
  touchedFields: Record<string, boolean> = {};

  roleLabels: { [key: string]: string } = {
    [Role.MASTER]: 'Maestro',
    [Role.ADMIN]: 'Administrador',
    [Role.GUEST]: 'Invitado',
    [Role.WAREHOUSE]: 'Bodega',
    [Role.SELLER]: 'Vendedor'
  };

  constructor(
    private userService: UserService,
    private notification: NotificationService,
    private tooltipService: TooltipService,
    private renderer: Renderer2) {
    this.user = this.initializeUser();
  }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltipService.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.userService.getListUsers().subscribe({
      next: (list) => {
        this.userList = list;
        this.existingMasterUser = !this.userList.some(user => user.role === Role.MASTER);
      },
      error: (error) => {
        console.log("Error, no se encontró una registro de usuarios");
      }
    });
  }

  getSelectedUser(selectedUser: User): void {
    this.user = selectedUser;
    this.showForm = true;
    this.addUserButton = false;
  }

  createUser(): void{
    if (this.validator.validateForm(this.user)) {
      const newUser = this.user;
      this.userService.addUser(newUser).subscribe({
        next: (response) =>{
          console.log('Usuario agregado exitosamente:', response);
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getUsers();
          this.resetForms();
        }, error:(error) => {
          const message = error.error?.message || error.error?.error;
          console.error('Error al agregar el usuario:', error);
          this.notification.showErrorToast(`Error al agregar usuario \n${message}`, 'top', 5000);
        }
      });
    }else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  updateUser(): void {
    if (this.user && this.validator.validateForm(this.user)) {
      const updateUser = { ...this.user };
      this.userService.updateUser(updateUser).subscribe({
        next: (response) =>{
          console.log('Se actualizo el cliente: ', updateUser);
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getUsers();
          this.resetForms();
        }, error: (error) => {
          const message = error.error?.message || error.error?.error;
          this.notification.showErrorToast(`Error al actualizar usuario \n${message}`, 'top', 5000);
          console.log('Error al actualizar usuario: ', message);
        }
      });
    }
  }

  activateUser(selectedUser: User): void {
    if(selectedUser){
      this.userService.activeUser(selectedUser).subscribe({
        next: (response) => {
          console.log("Usuario Activado");
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getUsers();
        }, error: (error) => {
          const message = error.error?.message || error.error?.error;
          console.log('Error al activar usuario: ', message);
          this.notification.showErrorToast(`Error al activar al usuario \n${message}`, 'top', 5000);
        }
      });
    }
  }

  deleteUser(selectedUser: User): void {
    if (selectedUser) {
      this.notification.showConfirmation(
        "¿Estas seguro?",
        "No podrás revertir la acción!",
        "Si eliminar!",
        "Cancelar"
      ).then((result) => {
        if (result.isConfirmed) {
          this.userService.deleteUser(selectedUser).subscribe({
            next: (response) => {
              console.log('Usuario eliminado exitosamente:', response);
              this.notification.showSuccessToast(response.message, 'top', 3000);
              this.getUsers();
            }, error: (error) => {
              const message = error.error?.message || error.error?.error;
              console.log('Error al eliminar cliente: ', message);
              this.notification.showErrorToast(`Error al eliminar usuario \n${message}`, 'top', 5000);
            }
          });
        }
      });
    }
  }

  resetForms(): void {
    this.addUserButton = true;
    this.showForm = false;
    this.initializeUser();
    this.touchedFields = {};
  }

  initializeUser(): User {
    return this.user = {
      id: 0,
      date: '',
      name: '',
      surname: '',
      username: '',
      rut: '',
      email: '',
      password: '',
      token: '',
      status: true,
      role: null
    };
  }
}
