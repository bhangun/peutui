#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# run-demo.sh  —  Build and run the peutui demo CLI
#
# Usage:
#   ./run-demo.sh          # build all modules then launch the demo
#   ./run-demo.sh --dev    # launch in Quarkus dev mode (hot-reload)
#   ./run-demo.sh --skip   # skip build, run last compiled jar directly
#   ./run-demo.sh --help   # show this message
#
# The demo opens a full-screen chat REPL against a mock streaming provider.
# Press Ctrl+C to quit.  Quarkus console logging is suppressed so it
# doesn't clutter the TUI — see quarkus.log.file.* for structured log output.
# ---------------------------------------------------------------------------
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ---- Colour helpers --------------------------------------------------------
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}[peutui]${NC} $*"; }
success() { echo -e "${GREEN}[peutui]${NC} $*"; }
warn()    { echo -e "${YELLOW}[peutui]${NC} $*"; }
error()   { echo -e "${RED}[peutui] ERROR:${NC} $*" >&2; }

# ---- Parse arguments -------------------------------------------------------
MODE="run"   # run | dev | skip
for arg in "$@"; do
    case "$arg" in
        --dev)    MODE="dev" ;;
        --skip)   MODE="skip" ;;
        --help|-h)
            echo "Usage:"
            echo "  ./run-demo.sh          — build all modules then launch the demo"
            echo "  ./run-demo.sh --dev    — launch in Quarkus dev mode (hot-reload)"
            echo "  ./run-demo.sh --skip   — skip build, run last compiled jar directly"
            echo "  ./run-demo.sh --help   — show this message"
            echo ""
            echo "The demo opens a full-screen chat REPL against a mock streaming provider."
            echo "Press Ctrl+C to quit."
            exit 0
            ;;
        *)
            warn "Unknown argument: $arg  (use --help for usage)"
            ;;
    esac
done

# ---- Check prerequisites ---------------------------------------------------
if ! command -v java &>/dev/null; then
    error "Java not found on PATH. Peutui requires Java 21+."
    exit 1
fi
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [[ "$JAVA_VER" -lt 21 ]]; then
    error "Java 21+ is required (found Java $JAVA_VER)."
    exit 1
fi

if ! command -v mvn &>/dev/null; then
    error "Maven (mvn) not found on PATH."
    exit 1
fi

# ---- Build -----------------------------------------------------------------
if [[ "$MODE" != "skip" ]]; then
    info "Building peutui modules…"
    # Build the whole multi-module project from the parent pom, skipping tests
    # to keep the feedback loop short.  The -q flag reduces noise; remove it
    # if you need to debug a build failure.
    mvn -q -DskipTests clean package \
        || { error "Build failed. Re-run without -q to see details: mvn -DskipTests clean package"; exit 1; }
    success "Build complete."
fi

# ---- Launch ----------------------------------------------------------------
DEMO_JAR="$SCRIPT_DIR/peutui-demo-cli/target/quarkus-app/quarkus-run.jar"

if [[ "$MODE" == "dev" ]]; then
    info "Starting peutui demo in Quarkus dev mode (hot-reload enabled)…"
    info "Press Ctrl+C to quit."
    cd "$SCRIPT_DIR/peutui-demo-cli"
    exec mvn quarkus:dev

elif [[ "$MODE" == "skip" || "$MODE" == "run" ]]; then
    if [[ ! -f "$DEMO_JAR" ]]; then
        error "Demo jar not found at: $DEMO_JAR"
        error "Run without --skip first to build the project."
        exit 1
    fi
    info "Starting peutui demo…"
    info "Press Ctrl+C to quit."
    echo ""
    # -Djava.awt.headless=true avoids any AWT initialisation on macOS headless VMs.
    # The JVM TTY flags ensure raw terminal input works correctly.
    exec java \
        -Djava.awt.headless=true \
        -Djline.terminal=jline.UnixTerminal \
        -jar "$DEMO_JAR"
fi
