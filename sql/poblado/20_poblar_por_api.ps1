# ============================================================
# Crea asignaciones y sesiones a traves del Gateway (puerto 9000).
# Requiere: Gateway + MS Usuario + MS Rutina + MS Entrenamiento + MS Notificaciones iniciados
# y las BDs ya limpiadas/pobladas (scripts 10 a 13).
# Genera: planes de la Estrategia (Principiante/Intermedio/Avanzado) y notificaciones (Observer).
# ============================================================
$ErrorActionPreference = 'Stop'
# Si algo falla, muestra el error y espera (para que la ventana no se cierre)
trap {
    Write-Host ''
    Write-Host '*** ERROR ***' -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ('Linea: ' + $_.InvocationInfo.ScriptLineNumber)
    Read-Host 'Copia el mensaje de arriba y enviamelo. Enter para cerrar'
    exit 1
}
$gw = 'http://localhost:9000/api'

function Llamar($metodo, $url, $cuerpo) {
    if ($cuerpo -ne $null) {
        $json = $cuerpo | ConvertTo-Json -Compress
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
        return Invoke-RestMethod -Method $metodo -Uri $url -ContentType 'application/json' -Body $bytes
    }
    return Invoke-RestMethod -Method $metodo -Uri $url
}

# GET que siempre devuelve una lista plana (Windows PowerShell 5.1 a veces anida los arrays)
function Lista($url) {
    $r = Invoke-RestMethod -Method GET -Uri $url
    $plana = @()
    foreach ($x in @($r)) { foreach ($y in @($x)) { $plana += $y } }
    return $plana
}

Write-Host '--- Verificando servicios ---'
try {
    $usuarios = @(Lista "$gw/usuarios")
    $rutinas  = @(Lista "$gw/rutinas/rutinas")
} catch {
    Write-Host 'No se pudo leer usuarios o rutinas. Revisa que Gateway y microservicios esten iniciados.' -ForegroundColor Red
    Write-Host $_.Exception.Message
    Read-Host 'Enter para salir'
    exit 1
}

function IdUsuario($email) {
    $u = $usuarios | Where-Object { $_.email -eq $email }
    if (-not $u) { throw "No existe el usuario $email. Ejecuta antes 10_usuario_limpiar_y_poblar.sql" }
    return [int]$u.id
}
function IdRutina($nombre) {
    $r = $rutinas | Where-Object { $_.nombre -eq $nombre }
    if (-not $r) { throw "No existe la rutina $nombre. Ejecuta antes 11_rutina_limpiar_y_poblar.sql" }
    return [int]$r.id
}

$hoy    = Get-Date
$inicio = $hoy.AddDays(-14).ToString('yyyy-MM-dd')
$fin    = $hoy.AddDays(14).ToString('yyyy-MM-dd')

# (cliente, rutina) -> el nivel de la rutina decide la estrategia
$casos = @(
    @{ email = 'adela@gym.com';   rutina = 'Full Body Inicial';        sesiones = @('COMPLETADO','COMPLETADO','PENDIENTE') },
    @{ email = 'marcos@gym.com';  rutina = 'Fuerza Tren Superior';     sesiones = @('COMPLETADO','PENDIENTE') },
    @{ email = 'daniela@gym.com'; rutina = 'Hipertrofia Avanzada';     sesiones = @('COMPLETADO','COMPLETADO','COMPLETADO','PENDIENTE') },
    @{ email = 'marcos@gym.com';  rutina = 'Piernas y Core';           sesiones = @('PENDIENTE') }
)

$creadas = @()
foreach ($c in $casos) {
    $idU = IdUsuario $c.email
    $idR = IdRutina $c.rutina
    $asig = Llamar 'POST' "$gw/entrenamientos/asignaciones" @{
        Id_Usuario = $idU; Id_Rutina = $idR; fechaInicio = $inicio; fechaFin = $fin; estado = 'ACTIVA'
    }
    $idA = [int]$asig.id
    Write-Host ("Asignacion {0}: {1} -> {2}" -f $idA, $c.email, $c.rutina) -ForegroundColor Green
    $creadas += $idA

    $dia = 12
    foreach ($estado in $c.sesiones) {
        $fecha = $hoy.AddDays(-$dia).ToString('yyyy-MM-dd')
        $dia = $dia - 4
        Llamar 'POST' "$gw/entrenamientos/sesiones" @{ Id_Asignacion = $idA; fecha = $fecha; estado = $estado } | Out-Null
        Write-Host ("   sesion {0} ({1})" -f $fecha, $estado)
    }
}

# Prueba de actualizarEstado: la ultima sesion PENDIENTE de la asignacion de Daniela se marca COMPLETADO
$idDaniela = $creadas[2]
$ses = @(Lista "$gw/entrenamientos/sesiones/$idDaniela")
$pendiente = $ses | Where-Object { $_.estado -eq 'PENDIENTE' } | Select-Object -First 1
if ($pendiente) {
    Llamar 'PUT' "$gw/entrenamientos/sesiones/$($pendiente.id)" $null | Out-Null
    Write-Host ("Sesion {0} de la asignacion {1} marcada COMPLETADO (PUT)" -f $pendiente.id, $idDaniela) -ForegroundColor Green
}

Write-Host ''
Write-Host '--- Planes generados por la Estrategia ---'
foreach ($idA in $creadas) {
    $p = Llamar 'GET' "$gw/entrenamientos/asignaciones/$idA" $null
    Write-Host ("Asignacion {0}: estrategia={1}, factorSeries={2}, sesionesPorSemana={3}, sesionesEsperadas={4}" -f `
        $idA, $p.plan.estrategia, $p.plan.factorSeries, $p.plan.sesionesPorSemana, $p.plan.sesionesEsperadas)
}

Write-Host ''
Write-Host '--- Notificaciones generadas por el Observer ---'
$nInstr = @(Lista "$gw/notificaciones?rol=INSTRUCTOR").Count
Write-Host ("Instructores (rol INSTRUCTOR): {0}" -f $nInstr)
foreach ($email in @('adela@gym.com','marcos@gym.com','daniela@gym.com')) {
    $n = @(Lista "$gw/notificaciones?usuarioId=$(IdUsuario $email)").Count
    Write-Host ("{0}: {1}" -f $email, $n)
}

Write-Host ''
Write-Host 'LISTO. Abre el frontend para verlo. Contrasena de los usuarios nuevos: 123456' -ForegroundColor Cyan
Read-Host 'Enter para cerrar'
