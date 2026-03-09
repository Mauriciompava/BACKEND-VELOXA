
const express = require('express');
const cors = require('cors');
const app = express();
const db = require('./config/db');

app.use(cors());
app.use(express.json());

// Routes
app.get('/api/tracking/:id', (req, res) => {
    const { id } = req.params;
    // Logic to fetch tracking from MySQL
    res.json({ tracking_number: id, status: 'In Transit', location: 'Central Hub' });
});

const PORT = 5000;
app.listen(PORT, () => console.log(`Veloxa Server running on port ${PORT}`));
