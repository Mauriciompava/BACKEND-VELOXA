
const express = require('express');
const cors = require('cors');
const app = express();
const db = require('./config/db');

app.use(cors());
app.use(express.json());

const bcrypt = require('bcryptjs');

// Routes
// 0. Autenticación - Registro
app.post('/api/auth/register', async (req, res) => {
    try {
        const { name, email, password, role } = req.body;

        // Verificar si el usuario ya existe
        const [existing] = await db.execute('SELECT id FROM users WHERE email = ?', [email]);
        if (existing.length > 0) {
            return res.status(400).json({ success: false, message: 'El correo ya está registrado' });
        }

        // Hash de la contraseña
        const hashedPassword = await bcrypt.hash(password, 10);

        // Insertar usuario
        const [result] = await db.execute(
            'INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)',
            [name, email, hashedPassword, role || 'admin']
        );

        res.status(201).json({
            success: true,
            message: 'Usuario registrado exitosamente',
            data: { id: result.insertId, name, email }
        });
    } catch (error) {
        console.error('Error en registro:', error);
        res.status(500).json({ 
            success: false, 
            message: 'Error técnico: ' + error.message 
        });
    }
});

// 0. Autenticación - Login
app.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const [users] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);
        if (users.length === 0) {
            return res.status(401).json({ success: false, message: 'Credenciales inválidas' });
        }

        const user = users[0];
        const isMatch = await bcrypt.compare(password, user.password);

        if (!isMatch) {
            return res.status(401).json({ success: false, message: 'Credenciales inválidas' });
        }

        res.json({
            success: true,
            message: 'Login exitoso',
            data: { id: user.id, name: user.name, email: user.email, role: user.role }
        });
    } catch (error) {
        console.error('Error en login:', error);
        res.status(500).json({ success: false, message: 'Error en el servidor' });
    }
});

// 1. Crear Nuevo Envío
app.post('/api/shipments', async (req, res) => {
    try {
        const { 
            origin, destination, weight, serviceType, 
            recipient, phone, email, address, items, 
            valueDeclaration, insurance, estimatedCost 
        } = req.body;

        const trackingNumber = 'VEL-' + Date.now();
        
        const [result] = await db.execute(
            `INSERT INTO shipments 
            (tracking_number, origin, destination, weight, status, recipient_name, recipient_phone, recipient_email, recipient_address, items_description, value_declared, insurance, estimated_cost) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [
                trackingNumber, origin, destination, weight, 'pending', 
                recipient, phone, email, address, items, 
                valueDeclaration || 0, insurance ? 1 : 0, estimatedCost || 0
            ]
        );

        res.status(201).json({
            success: true,
            message: 'Envío creado exitosamente',
            data: {
                id: result.insertId,
                trackingNumber,
                estimatedCost
            }
        });
    } catch (error) {
        console.error('Error al crear envío:', error);
        res.status(500).json({
            success: false,
            message: 'Error interno del servidor al procesar el envío',
            error: error.message
        });
    }
});

// 2. Obtener Estado de Envío (Rastreo)
app.get('/api/tracking/:trackingNumber', async (req, res) => {
    try {
        const { trackingNumber } = req.params;
        const [rows] = await db.execute(
            'SELECT * FROM shipments WHERE tracking_number = ?',
            [trackingNumber]
        );

        if (rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Número de seguimiento no encontrado'
            });
        }

        res.json({
            success: true,
            shipment: rows[0]
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error al consultar el rastreo',
            error: error.message
        });
    }
});

const PORT = 5000;
app.listen(PORT, () => console.log(`Veloxa Server running on port ${PORT}`));
