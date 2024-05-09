package com.alex.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

class WebSocket implements Screen {
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;
    private Skin skin;
    private Label ip;

    public WebSocket() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(25);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.getFont("default-font").getData().setScale(1.5f);

        Dialog dialog = new Dialog("Get IP", skin, "dialog") {
            @Override
            protected void result(Object object) {
                this.hide();
                if (object.equals("si")) {
                    requestIP();
                }
            }
        };

        ip = new Label("Presiona el boton para obtener la ip", skin);
        ip.setFontScale(3); // Ajusta el tamaño del texto
        dialog.getContentTable().add(ip).pad(0).height(300).width(800).row();
        dialog.setColor(1, 0, 0, 1);
        TextButton boton = new TextButton(" Presiona ", skin);
        boton.getLabel().setFontScale(3); // Ajusta el tamaño del texto del botón
        boton.getSkin().setScale(3);
        dialog.button(boton, "si");
        dialog.getContentTable().setScale(350);
        dialog.padTop(100).pad(300);  // Aumenta el padding del diálogo
        dialog.getCells().get(0).padBottom(20);  // Aumenta el padding debajo del texto del diálogo
        dialog.getButtonTable().defaults().width(400).height(200);  // Tamaños de los botones
        dialog.show(stage);
    }

    private void requestIP() {
        HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl("https://api.ipify.org?format=json");

        Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                JsonReader jsonReader = new JsonReader();
                JsonValue json = jsonReader.parse(httpResponse.getResultAsString());
                String message = "Tu IP es: " + json.getString("ip");
                showResultDialog(message);
            }

            @Override
            public void failed(Throwable t) {
                showResultDialog("Error: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                showResultDialog("Llamada HTTP cancelada");
            }
        });
    }

    private void showResultDialog(String message) {
        Dialog resultDialog = new Dialog("Resultado", skin);
        resultDialog.text(message);
        resultDialog.button("OK", true);
        resultDialog.padTop(30).pad(30);
        resultDialog.getButtonTable().defaults().width(150).height(50);
        resultDialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        stage.dispose();
    }
}
