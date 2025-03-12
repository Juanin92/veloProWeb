import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { UserService } from '../../services/User/user.service';
import { User } from '../../models/Entity/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserValidator } from '../../validation/user-validator';
import { Role } from '../../models/enum/role';
import { TooltipService } from '../../utils/tooltip.service';
import { NotificationService } from '../../utils/notification-service.service';
import { UserDTO } from '../../models/DTO/user-dto';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit, AfterViewInit {

  userList: UserDTO[] = [];
  addUserButton: boolean = true;
  showForm: boolean = false;
  existingMasterUser: boolean = true;
  validator = UserValidator;
  roles: string[] = Object.values(Role);
  user: UserDTO;
  selectedUser: UserDTO | null = null;
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

  /**
   * Obtiene la lista de usuarios 
   * verifica si existe un usuario con el rol de MASTER.
   */
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

  /**
   * Establece el usuario seleccionado, muestra el formulario de edición,
   * Oculta botón de agregar usuario.
   * @param selectedUser 
   */
  getSelectedUser(selectedUser: UserDTO): void {
    this.user = selectedUser;
    this.showForm = true;
    this.addUserButton = false;
  }

  /**
   * Crea un nuevo usuario después de validar el formulario.
   */
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

  /**
   * Actualiza un usuario existente después de validar el formulario.
   */
  // updateUser(): void {
  //   if (this.user && this.validator.validateForm(this.user)) {
  //     const updateUser = { ...this.user };
  //     this.userService.updateUser(updateUser).subscribe({
  //       next: (response) =>{
  //         console.log('Se actualizo el cliente: ', updateUser);
  //         this.notification.showSuccessToast(response.message, 'top', 3000);
  //         this.getUsers();
  //         this.resetForms();
  //       }, error: (error) => {
  //         const message = error.error?.message || error.error?.error;
  //         this.notification.showErrorToast(`Error al actualizar usuario \n${message}`, 'top', 5000);
  //         console.log('Error al actualizar usuario: ', message);
  //       }
  //     });
  //   }
  // }

  /**
   * Activa un usuario seleccionado
   * @param selectedUser - usuario que se desea activar.
   */
  activateUser(selectedUser: UserDTO): void {
    if(selectedUser){
      this.userService.activeUser(selectedUser.username).subscribe({
        next: (response) => {
          console.log("Usuario Activado");
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getUsers();
        }, error: (error) => {
          const message = error.error?.message || error.error?.error || error?.error;
          console.log('Error al activar usuario: ', message);
          this.notification.showErrorToast(`Error al activar al usuario \n${message}`, 'top', 5000);
        }
      });
    }
  }

  /**
   * Elimina un usuario seleccionado después de la confirmación del usuario.
   * @param selectedUser - usuario que se desea eliminar.
   */
  deleteUser(selectedUser: UserDTO): void {
    if (selectedUser) {
      this.notification.showConfirmation(
        "¿Estas seguro?",
        "No podrás revertir la acción!",
        "Si eliminar!",
        "Cancelar"
      ).then((result) => {
        if (result.isConfirmed) {
          this.userService.deleteUser(selectedUser.username).subscribe({
            next: (response) => {
              console.log('Usuario eliminado exitosamente:', response);
              this.notification.showSuccessToast(response.message, 'top', 3000);
              this.getUsers();
            }, error: (error) => {
              const message = error.error?.message || error.error?.error || error?.error;
              console.log('Error al eliminar cliente: ', message);
              this.notification.showErrorToast(`Error al eliminar usuario \n${message}`, 'top', 5000);
            }
          });
        }
      });
    }
  }

  /**
   * Restablece los formularios y los campos relacionados con el usuario.
   */
  resetForms(): void {
    this.addUserButton = true;
    this.showForm = false;
    this.initializeUser();
    this.touchedFields = {};
  }

  /**
   * Inicializa un nuevo objeto de usuario con valores predeterminados.
   * @returns - Usuario inicializado.
   */
  initializeUser(): UserDTO {
    return this.user = {
      name: '',
      surname: '',
      username: '',
      rut: '',
      email: '',
      token: '',
      status: true,
      role: null,
      date: ''
    };
  }
}
