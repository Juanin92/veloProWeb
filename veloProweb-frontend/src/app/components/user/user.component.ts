import { AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { UserService } from '../../services/User/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserValidator } from '../../validation/user-validator';
import { Role } from '../../models/enum/role';
import { TooltipService } from '../../utils/tooltip.service';
import { NotificationService } from '../../utils/notification-service.service';
import { UserDTO } from '../../models/DTO/user-dto';
import * as bootstrap from 'bootstrap';
import { UserPermissionsService } from '../../services/Permissions/user-permissions.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit, AfterViewInit {

  @ViewChild('userTableFilter') dropdownButton!: ElementRef;
  userList: UserDTO[] = [];
  filteredList: UserDTO[] = [];
  addUserButton: boolean = true;
  showForm: boolean = false;
  existingMasterUser: boolean = true;
  validator = UserValidator;
  roles: string[] = Object.values(Role);
  user: UserDTO;
  selectedUser: UserDTO | null = null;
  touchedFields: Record<string, boolean> = {};
  dropdownInstance!: bootstrap.Dropdown;
  sortDate: boolean = true;
  sortName: boolean = true;
  sortPosition: boolean = true;
  textFilter: string = '';

  roleLabels: { [key: string]: string } = {
    [Role.MASTER]: 'Maestro',
    [Role.ADMIN]: 'Administrador',
    [Role.GUEST]: 'Invitado',
    [Role.WAREHOUSE]: 'Bodega',
    [Role.SELLER]: 'Vendedor'
  };

  constructor(
    private userService: UserService,
    protected permission: UserPermissionsService,
    private notification: NotificationService,
    private tooltipService: TooltipService,
    private renderer: Renderer2) {
    this.user = this.initializeUser();
  }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltipService.initializeTooltips();
    });
    if (this.dropdownButton) {
          this.dropdownInstance = new bootstrap.Dropdown(this.dropdownButton.nativeElement);
    }
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
        if(localStorage.getItem('role') !== Role.MASTER){
          this.filteredList = list.filter(user => user.role !== Role.MASTER);
        }else{
          this.filteredList = list;
        }
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
          const message = error.error?.message || error.error?.error || error?.error;
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
  updateUserByAdmin(): void {
    if (this.user && this.validator.validateForm(this.user)) {
      const updateUser = { ...this.user };
      this.userService.updateUserByAdmin(updateUser).subscribe({
        next: (response) =>{
          console.log('Se actualizo el cliente: ', updateUser);
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getUsers();
          this.resetForms();
        }, error: (error) => {
          const message = error.error?.message || error.error?.error || error?.error;
          this.notification.showErrorToast(`Error al actualizar usuario \n${message}`, 'top', 5000);
          console.log('Error al actualizar usuario: ', message);
        }
      });
    }
  }

  confirmDelete(user: UserDTO): void {
    this.selectedUser = user;
    this.notification.showConfirmation(
      "¿Estás seguro?",
      "No podrás revertir la acción!",
      "Sí, eliminar!",
      "Cancelar"
    ).then((result) => {
      if (result.isConfirmed) {
        if (this.selectedUser) {
          this.userService.deleteUser(this.selectedUser).subscribe({
            next: (response) => {
              console.log('Usuario eliminado exitosamente:', response);
              this.notification.showSuccessToast(response.message, 'top', 3000);
              this.getUsers();
            },
            error: (error) => {
              const message = error.error?.message || error.error?.error || error?.error;
              console.log('Error al eliminar usuario: ', message);
              this.notification.showErrorToast(`Error al eliminar usuario \n${message}`, 'top', 5000);
            }
          });
        }
      }
      this.selectedUser = null;
    });
  }

  /**
   * Activa un usuario seleccionado
   */
  activateUser(user: UserDTO): void {
    this.selectedUser = user;
    if(this.selectedUser){
      this.userService.activeUser(this.selectedUser).subscribe({
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
      this.selectedUser = null;
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

  toggleDropdown() {
    if (this.dropdownInstance) {
      this.dropdownInstance.toggle();
    }
  }

  toggleSortDate() {
    this.sortDate = !this.sortDate;
    this.filteredList.sort((a, b) => {
        const dateA = new Date(a.date).getTime();
        const dateB = new Date(b.date).getTime();
        return this.sortDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortName() {
    this.sortName = !this.sortName;
    this.filteredList.sort((a, b) => {
        const nameA = a.name.toLowerCase(); 
        const nameB = b.name.toLowerCase();
        if (this.sortName) {
          return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
      } else {
          return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
      }
    });
  }

  toggleSortPosition() {
    this.filteredList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  searchFilterUser(): void {
    if (this.textFilter.trim() === '') {
      this.filteredList = this.userList;
    } else {
      this.filteredList = this.userList.filter(user =>
        user.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        user.surname.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        user.rut.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        user.username.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        user.email.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        (this.textFilter.toLowerCase() === 'activo' && user.status) ||
        (this.textFilter.toLowerCase() === 'inactivo' && !user.status)
      );
    }
  }
}
