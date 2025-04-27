clean=''
dockerize=''
show=''
verbose=''
project=''

execute_build() {
  command=()
  if [ "${clean}" == 'true' ]; then
    if [ "${project}" != '' ]; then
      printf "Limpiando proyecto ${project}\n"
      gradle :${project}:clean
      docker container stop $(docker container ls | grep -E "${project}" | awk '{print $1}')
      docker image rm -f $(docker image ls | grep -E "${project}|none" | awk '{print $3}')
    else
      printf "Limpiando proyectos\n"
      gradle clean
      docker compose down
      docker image rm -f $(docker image ls | grep -E 'e-health|none' | awk '{print $3}')
    fi
  fi
  command+=('bootJar')
  if [ "${verbose}" == 'true' ]; then
    command+=('--info')
  fi
  if [ "${show}" == 'true' ]; then
    printf "Proyectos detectados:\n"
    printf "\n______________________________\n\n"
    gradle projects
    printf "\n______________________________\n"
  fi
  printf "Ejecutando gradle ${command[@]}\n"
  printf "\n______________________________\n\n"
  gradle "${command[@]}"
  printf "\n______________________________\n"
  if [ "${dockerize}" == 'true' ]; then
    if [ "${project}" != '' ]; then
      printf "Reiniciando contenedor ${project}\n"
      docker container restart $(docker container ls | grep -E "${project}" | awk '{print $1}')
    else
      printf "Levantando contenedores\n"
      docker compose up -d
    fi
  fi
}

execute_error() {
  printf "Las opciones disponibles son:
          -c para limpiar el proyecto
          -d para levantar los contenedores
          -s para mostrar el listado de proyectos agregados
          -v para mostrar información de compilación
          -p para indicar el proyecto a limpiar"
  exit 1
}

while getopts 'cdvsp:' flag; do
  case "${flag}" in
    c) clean='true' ;;
    d) dockerize='true' ;;
    s) show='true' ;;
    v) verbose='true' ;;
    p) project="${OPTARG}" ;;
    *) execute_error ;;
  esac
done

execute_build