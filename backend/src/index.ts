// Servidor Express + Socket.io - Punto de entrada del backend
import "reflect-metadata";
import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import http from "http";
import { Server as SocketIOServer } from "socket.io";
import { AppDataSource } from "./data-source";
import usersRouter from "./routes/users";
import reunionesRouter from "./routes/reuniones";
import horariosRouter from "./routes/horarios";
import modulosRouter from "./routes/modulos";
import ciclosRouter from "./routes/ciclos";
import matriculacionesRouter from "./routes/matriculaciones";
import path from "path";

dotenv.config();

const app = express();
const server = http.createServer(app);
const io = new SocketIOServer(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST", "PUT", "DELETE"],
  },
});

const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const publicPath = path.join(__dirname, "..", "public");
const altPublicPath = path.join(__dirname, "..", "..", "public");
app.use("/public", express.static(publicPath));
app.use("/public", express.static(altPublicPath));

app.use(
  "/public",
  express.static(path.join(__dirname, "..", "..", "..", "public")),
);

app.use("/api/users", usersRouter);
app.use("/api/reuniones", reunionesRouter);
app.use("/api/horarios", horariosRouter);
app.use("/api/modulos", modulosRouter);
app.use("/api/ciclos", ciclosRouter);
app.use("/api/matriculaciones", matriculacionesRouter);

app.get("/api/health", (req, res) => {
  res.json({ status: "OK", message: "ElorServ API está funcionando" });
});

io.on("connection", (socket) => {
  console.log("🔌 Cliente conectado (ElorES):", socket.id);

  socket.on("teacher:login", (data) => {
    console.log("👨‍🏫 Profesor conectado:", data.teacherId);
    socket.join(`teacher_${data.teacherId}`);
    socket.emit("login:success", { message: "Conectado correctamente" });
  });

  socket.on("reunion:request", (data) => {
    console.log("📅 Nueva solicitud de reunión:", data);
    io.to(`teacher_${data.teacherId}`).emit("reunion:new", data);
  });

  socket.on("reunion:response", (data) => {
    console.log("📝 Respuesta a reunión:", data);
    io.emit("reunion:updated", data);
  });

  socket.on("schedule:request", (data) => {
    console.log("📋 Solicitud de horario:", data.teacherId);
  });

  socket.on("disconnect", () => {
    console.log("🔌 Cliente desconectado:", socket.id);
  });
});

export { io };

AppDataSource.initialize()
  .then(() => {
    console.log("Conexión a MySQL establecida correctamente");

    server.listen(PORT, () => {
      console.log(`ElorServ corriendo en http://localhost:${PORT}`);
    });
  })
  .catch((error) => {
    console.error("Error al conectar con la base de datos:", error);
  });
