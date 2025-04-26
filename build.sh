clean=''
dockerize=''
show=''
verbose=''

execute_build() {
  command=()
  if [ "${clean}" == 'true' ]; then
    command+=('clean')
  fi
  command+=('assemble')
  if [ "${verbose}" == 'true' ]; then
    command+=('--info')
  fi
  if [ "${show}" == 'true' ]; then
    printf "Proyectos detectados:"
    gradle projects
    printf "\n______________________________\n"
  fi
  printf "Ejecutando `gradle ${command[@]}`"
  gradle "${command[@]}"
  if [ "${dockerize}" == 'true' ]; then
    printf "Levantando contenedores"
    docker compose up -d
  fi
}

execute_error() {
  printf "Las opciones disponibles son:
          -c para limpiar el proyecto
          -d para levantar los contenedores
          -s para mostrar el listado de proyectos agregados
          -v para mostrar información de compilación"
  exit 1
}

while getopts 'cdvs' flag; do
  case "${flag}" in
    c) clean='true' ;;
    d) dockerize='true' ;;
    s) show='true' ;;
    v) verbose='true' ;;
    *) execute_error ;;
  esac
done

execute_build