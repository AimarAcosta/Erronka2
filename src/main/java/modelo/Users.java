package modelo;

// IMPORTACIONES OBLIGATORIAS
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "users")
public class Users implements java.io.Serializable {

	// ==========================================
	// CAMPOS DE LA BASE DE DATOS (CON @Expose PARA ANDROID)
	// ==========================================

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Expose
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_id")
	@Expose
	private Tipos tipos;

	@Column(name = "email")
	@Expose
	private String email;

	@Column(name = "username")
	@Expose
	private String username;

	@Column(name = "password")
	@Expose
	private String password;

	@Column(name = "nombre")
	@Expose
	private String nombre;

	@Column(name = "apellidos")
	@Expose
	private String apellidos;

	@Column(name = "dni")
	@Expose
	private String dni;

	@Column(name = "direccion")
	@Expose
	private String direccion;

	@Column(name = "telefono1")
	@Expose
	private String telefono1;

	@Column(name = "telefono2")
	@Expose
	private String telefono2;

	@Column(name = "argazkia_url") // Mapeado exacto a tu BBDD
	@Expose
	private String argazkiaUrl;

	@Column(name = "created_at")
	@Expose
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@Expose
	private Timestamp updatedAt;

	// ==========================================
	// CAMPOS EXTRAS (SOLO PARA ENVIAR AL MÓVIL)
	// ==========================================
	// @Transient: No existen en la tabla 'users'
	// @Expose: Sí se envían en el JSON

	@Transient
	@Expose
	private String ciclo; // Nombre del ciclo (String)

	@Transient
	@Expose
	private Integer curso; // Número del curso

	// Getters y Setters de los extras
	public String getCiclo() {
		return ciclo;
	}

	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getCurso() {
		return curso;
	}

	public void setCurso(Integer curso) {
		this.curso = curso;
	}

	// ==========================================
	// LISTAS RELACIONADAS (IGNORADAS EN EL JSON)
	// ==========================================

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private transient Set matriculacioneses = new HashSet(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usersByAlumnoId")
	private transient Set reunionesesForAlumnoId = new HashSet(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usersByProfesorId")
	private transient Set horarioses = new HashSet(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usersByProfesorId")
	private transient Set reunionesesForProfesorId = new HashSet(0);

	// ==========================================
	// CONSTRUCTORES
	// ==========================================
	public Users() {
	}

	public Users(Tipos tipos, String email, String username, String password) {
		this.tipos = tipos;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public Users(Tipos tipos, String email, String username, String password, String nombre, String apellidos,
			String dni, String direccion, String telefono1, String telefono2, String argazkiaUrl, Timestamp createdAt,
			Timestamp updatedAt, Set matriculacioneses, Set reunionesesForAlumnoId, Set horarioses,
			Set reunionesesForProfesorId) {
		this.tipos = tipos;
		this.email = email;
		this.username = username;
		this.password = password;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.direccion = direccion;
		this.telefono1 = telefono1;
		this.telefono2 = telefono2;
		this.argazkiaUrl = argazkiaUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.matriculacioneses = matriculacioneses;
		this.reunionesesForAlumnoId = reunionesesForAlumnoId;
		this.horarioses = horarioses;
		this.reunionesesForProfesorId = reunionesesForProfesorId;
	}

	// ==========================================
	// GETTERS Y SETTERS STANDARD
	// ==========================================

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tipos getTipos() {
		return this.tipos;
	}

	public void setTipos(Tipos tipos) {
		this.tipos = tipos;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return this.apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono1() {
		return this.telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	public String getTelefono2() {
		return this.telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	public String getArgazkiaUrl() {
		return this.argazkiaUrl;
	}

	public void setArgazkiaUrl(String argazkiaUrl) {
		this.argazkiaUrl = argazkiaUrl;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Set getMatriculacioneses() {
		return this.matriculacioneses;
	}

	public void setMatriculacioneses(Set matriculacioneses) {
		this.matriculacioneses = matriculacioneses;
	}

	public Set getReunionesesForAlumnoId() {
		return this.reunionesesForAlumnoId;
	}

	public void setReunionesesForAlumnoId(Set reunionesesForAlumnoId) {
		this.reunionesesForAlumnoId = reunionesesForAlumnoId;
	}

	public Set getHorarioses() {
		return this.horarioses;
	}

	public void setHorarioses(Set horarioses) {
		this.horarioses = horarioses;
	}

	public Set getReunionesesForProfesorId() {
		return this.reunionesesForProfesorId;
	}

	public void setReunionesesForProfesorId(Set reunionesesForProfesorId) {
		this.reunionesesForProfesorId = reunionesesForProfesorId;
	}
}